package com.example.pokedex.feature.pokemonlist

import com.example.pokedex.core.mvi.UiEffect
import com.example.pokedex.core.mvi.UiEvent
import com.example.pokedex.core.mvi.UiState

/**
 * This class is responsible for PokemonListContract logic.
 * Part of the Clean Architecture structure.
 */
@androidx.compose.runtime.Immutable
data class PokemonListState(
    val searchQuery: String = "",
    val selectedType: String? = null,
    val availableTypes: List<String> = emptyList()
) : UiState

sealed interface PokemonListEvent : UiEvent {
    data class OnPokemonClicked(val pokemonId: Int) : PokemonListEvent
    data class OnSearchQueryChanged(val query: String) : PokemonListEvent
    data class OnTypeFilterSelected(val type: String?) : PokemonListEvent
}

sealed interface PokemonListEffect : UiEffect {
    data class NavigateToDetail(val pokemonId: Int) : PokemonListEffect
    data object NavigateToProfile : PokemonListEffect
    data object NavigateToFavorites : PokemonListEffect
}
