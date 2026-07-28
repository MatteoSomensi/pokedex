package com.example.pokedex.feature.pokemondetail

import androidx.lifecycle.viewModelScope
import com.example.pokedex.core.R
import com.example.pokedex.core.mvi.BaseViewModel
import com.example.pokedex.core.util.UiText
import com.example.pokedex.domain.repository.PokemonRepository
import com.example.pokedex.feature.pokemondetail.audio.PokemonCryPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Loads one Pokémon detail and coordinates favorite and cry-playback intentions.
 *
 * Favorite changes are optimistic: state updates immediately and rolls back to the previous model
 * if persistence fails. Audio playback is delegated to [PokemonCryPlayer], keeping Android media
 * resources outside Compose and tying their cleanup to the ViewModel lifecycle.
 */
@HiltViewModel
class PokemonDetailViewModel
    @Inject
    constructor(
        private val repository: PokemonRepository,
        private val cryPlayer: PokemonCryPlayer,
    ) : BaseViewModel<PokemonDetailState, PokemonDetailEvent, PokemonDetailEffect>() {
        override fun createInitialState(): PokemonDetailState = PokemonDetailState()

        override fun handleEvent(event: PokemonDetailEvent) {
            when (event) {
                is PokemonDetailEvent.LoadPokemon -> loadPokemon(id = event.id)
                is PokemonDetailEvent.ToggleFavorite -> toggleFavorite()
                is PokemonDetailEvent.PlayCry -> playCry()
            }
        }

        private fun playCry() {
            val currentPokemon = uiState.value.pokemon ?: return
            viewModelScope.launch {
                cryPlayer.play(url = currentPokemon.cryUrl).onFailure {
                    setEffect { PokemonDetailEffect.ShowPlaybackError }
                }
            }
        }

        private fun toggleFavorite() {
            val currentPokemon = uiState.value.pokemon ?: return
            val newFavoriteStatus = !currentPokemon.isFavorite

            setState {
                copy(pokemon = currentPokemon.copy(isFavorite = newFavoriteStatus))
            }

            viewModelScope.launch {
                val result =
                    repository.toggleFavoriteStatus(
                        id = currentPokemon.id,
                        isFavorite = newFavoriteStatus,
                    )
                if (result.isFailure) {
                    setState {
                        copy(
                            pokemon = currentPokemon,
                            errorMessage = UiText.StringResource(id = R.string.error_default),
                        )
                    }
                }
            }
        }

        private fun loadPokemon(id: Int) {
            cryPlayer.release()
            viewModelScope.launch {
                setState { copy(isLoading = true, errorMessage = null) }
                val result = repository.getPokemonDetail(id = id)
                if (result.isSuccess) {
                    setState {
                        copy(
                            isLoading = false,
                            pokemon = result.getOrNull(),
                        )
                    }
                } else {
                    setState {
                        copy(
                            isLoading = false,
                            errorMessage = UiText.StringResource(id = R.string.error_default),
                        )
                    }
                }
            }
        }

        override fun onCleared() {
            cryPlayer.release()
            super.onCleared()
        }
    }
