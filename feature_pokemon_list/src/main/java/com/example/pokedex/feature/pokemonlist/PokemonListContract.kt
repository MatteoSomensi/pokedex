package com.example.pokedex.feature.pokemonlist

import com.example.pokedex.core.mvi.UiEffect
import com.example.pokedex.core.mvi.UiEvent
import com.example.pokedex.core.mvi.UiState
import com.example.pokedex.domain.model.Pokemon


/**
 * This class is responsible for PokemonListContract logic.
 * Part of the Clean Architecture structure.
 */
data class PokemonListState(
    val isLoading: Boolean = false,
    val isFetchingNextPage: Boolean = false,
    val isEndReached: Boolean = false,
    val offset: Int = 0,
    val pokemonList: List<Pokemon> = emptyList(),
    val filteredPokemonList: List<Pokemon> = emptyList(),
    val searchQuery: String = "",
    val selectedType: String? = null,
    val errorMessage: Int? = null
) : UiState

sealed interface PokemonListEvent : UiEvent {
    data object LoadPokemon : PokemonListEvent
    data object LoadNextPage : PokemonListEvent
    data class OnPokemonClicked(val pokemonId: Int) : PokemonListEvent
    data class OnSearchQueryChanged(val query: String) : PokemonListEvent
    data class OnTypeFilterSelected(val type: String?) : PokemonListEvent
}

sealed interface PokemonListEffect : UiEffect {
    data class NavigateToDetail(val pokemonId: Int) : PokemonListEffect
}
