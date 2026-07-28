package com.example.pokedex.feature.favorite.impl.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.domain.model.Pokemon
import com.example.pokedex.domain.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavoriteUiState(
    val isLoading: Boolean = false,
    val favorites: List<Pokemon> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoriteUiState())
    val uiState: StateFlow<FavoriteUiState> = _uiState.asStateFlow()

    init {
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            repository.observeFavoritePokemonIds().collect {
                loadFavorites()
            }
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.getFavoritePokemonList()
            if (result.isSuccess) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        favorites = result.getOrNull() ?: emptyList()
                    ) 
                }
            } else {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        error = result.exceptionOrNull()?.message ?: "Errore caricamento"
                    ) 
                }
            }
        }
    }

    fun toggleFavorite(pokemon: Pokemon) {
        viewModelScope.launch {
            // Selezionando il preferito dalla schermata Preferiti, lo rimuoviamo.
            repository.toggleFavoriteStatus(pokemon.id, false)
        }
    }
}
