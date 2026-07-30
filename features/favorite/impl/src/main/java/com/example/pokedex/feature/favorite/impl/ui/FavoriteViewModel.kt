package com.example.pokedex.feature.favorite.impl.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.core.R
import com.example.pokedex.core.util.UiText
import com.example.pokedex.domain.model.Pokemon
import com.example.pokedex.domain.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Immutable presentation state for the favorites destination.
 *
 * @property favorites locally stored favorite Pokémon ordered by the repository.
 * @property error diagnostic message when loading fails.
 */
data class FavoriteUiState(
    val isLoading: Boolean = false,
    val favorites: List<Pokemon> = emptyList(),
    val error: UiText? = null,
)

/**
 * Keeps the favorites screen synchronized with locally persisted favorite IDs.
 *
 * Every favorite-ID emission reloads the complete favorite projection so the UI receives current
 * domain models rather than maintaining a second cache.
 */
@HiltViewModel
class FavoriteViewModel
    @Inject
    constructor(
        private val repository: PokemonRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(FavoriteUiState())

        /** Current loading, content, or error state. */
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
                            favorites = result.getOrNull() ?: emptyList(),
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = UiText.StringResource(R.string.error_loading),
                        )
                    }
                }
            }
        }

        /** Removes [pokemon] from favorites; the observed ID flow triggers the subsequent reload. */
        fun toggleFavorite(pokemon: Pokemon) {
            viewModelScope.launch {
                repository.toggleFavoriteStatus(pokemon.id, false)
            }
        }
    }
