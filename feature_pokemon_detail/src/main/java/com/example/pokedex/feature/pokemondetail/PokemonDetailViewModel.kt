package com.example.pokedex.feature.pokemondetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
data class PokemonDetailState(
    val isLoading: Boolean = false,
    val pokemon: Pokemon? = null,
    val errorMessage: Int? = null
)

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    val uiState: StateFlow<PokemonDetailState>
        field = MutableStateFlow(PokemonDetailState())

    fun loadPokemon(id: Int) {
        viewModelScope.launch {
            uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.getPokemonDetail(id)
            if (result.isSuccess) {
                uiState.update {
                    it.copy(
                        isLoading = false,
                        pokemon = result.getOrNull()
                    )
                }
            } else {
                uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = com.example.pokedex.core.R.string.error_default
                    )
                }
            }
        }
    }
}
