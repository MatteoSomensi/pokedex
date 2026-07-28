package com.example.pokedex.feature.pokemonlist

import androidx.compose.runtime.Immutable
import com.example.pokedex.core.mvi.UiEffect
import com.example.pokedex.core.mvi.UiEvent
import com.example.pokedex.core.mvi.UiState

/**
 * Immutable controls state for the paginated list.
 *
 * Paging content is exposed separately by [PokemonListViewModel.pagedPokemonFlow].
 *
 * @property selectedType `null` means that all Pokémon types are accepted.
 */
@Immutable
data class PokemonListState(
    val searchQuery: String = "",
    val selectedType: String? = null,
    val availableTypes: List<String> = emptyList(),
) : UiState

/** User intentions accepted by [PokemonListViewModel]. */
sealed interface PokemonListEvent : UiEvent {
    data class OnPokemonClicked(
        val pokemonId: Int,
    ) : PokemonListEvent

    data class OnSearchQueryChanged(
        val query: String,
    ) : PokemonListEvent

    data class OnTypeFilterSelected(
        val type: String?,
    ) : PokemonListEvent
}

/** One-shot navigation requests emitted by [PokemonListViewModel]. */
sealed interface PokemonListEffect : UiEffect {
    data class NavigateToDetail(
        val pokemonId: Int,
    ) : PokemonListEffect

    data object NavigateToProfile : PokemonListEffect

    data object NavigateToFavorites : PokemonListEffect
}
