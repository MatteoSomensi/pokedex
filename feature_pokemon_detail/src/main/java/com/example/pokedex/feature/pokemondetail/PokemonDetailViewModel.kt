package com.example.pokedex.feature.pokemondetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.core.R
import com.example.pokedex.core.util.UiText
import com.example.pokedex.domain.model.Pokemon
import com.example.pokedex.domain.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * This class is responsible for PokemonDetailViewModel logic.
 * Part of the Clean Architecture structure.
 */
import com.example.pokedex.core.mvi.BaseViewModel

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val repository: PokemonRepository
) : BaseViewModel<PokemonDetailState, PokemonDetailEvent, PokemonDetailEffect>() {

    override fun createInitialState(): PokemonDetailState = PokemonDetailState()

    override fun handleEvent(event: PokemonDetailEvent) {
        when (event) {
            is PokemonDetailEvent.LoadPokemon -> loadPokemon(event.id)
        }
    }

    private fun loadPokemon(id: Int) {
        viewModelScope.launch {
            setState { copy(isLoading = true, errorMessage = null) }
            val result = repository.getPokemonDetail(id)
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
                        errorMessage = UiText.StringResource(R.string.error_default)
                    )
                }
            }
        }
    }
}
