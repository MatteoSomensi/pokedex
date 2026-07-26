package com.example.pokedex.feature.pokemondetail


/**
 * ViewModel managing the state and logic for the Pokemon Detail screen.
 * Fetches and exposes detailed information about a specific Pokemon, including its stats, weight, and height.
 */
import androidx.lifecycle.viewModelScope
import com.example.pokedex.core.R
import com.example.pokedex.core.mvi.BaseViewModel
import com.example.pokedex.core.util.UiText
import com.example.pokedex.domain.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val repository: PokemonRepository
) : BaseViewModel<PokemonDetailState, PokemonDetailEvent, PokemonDetailEffect>() {

    override fun createInitialState(): PokemonDetailState = PokemonDetailState()

    override fun handleEvent(event: PokemonDetailEvent) {
        when (event) {
            is PokemonDetailEvent.LoadPokemon -> loadPokemon(id = event.id)
            is PokemonDetailEvent.ToggleFavorite -> toggleFavorite()
        }
    }

    private fun toggleFavorite() {
        val currentPokemon = uiState.value.pokemon ?: return
        val newFavoriteStatus = !currentPokemon.isFavorite

        setState {
            copy(pokemon = currentPokemon.copy(isFavorite = newFavoriteStatus))
        }

        viewModelScope.launch {
            val result = repository.toggleFavoriteStatus(
                id = currentPokemon.id,
                isFavorite = newFavoriteStatus
            )
            if (result.isFailure) {
                setState {
                    copy(
                        pokemon = currentPokemon,
                        errorMessage = UiText.StringResource(id = R.string.error_default)
                    )
                }
            }
        }
    }

    private fun loadPokemon(id: Int) {
        viewModelScope.launch {
            setState { copy(isLoading = true, errorMessage = null) }
            val result = repository.getPokemonDetail(id = id)
            if (result.isSuccess) {
                setState {
                    copy(
                        isLoading = false,
                        pokemon = result.getOrNull()
                    )
                }
            } else {
                setState {
                    copy(
                        isLoading = false,
                        errorMessage = UiText.StringResource(id = R.string.error_default)
                    )
                }
            }
        }
    }
}
