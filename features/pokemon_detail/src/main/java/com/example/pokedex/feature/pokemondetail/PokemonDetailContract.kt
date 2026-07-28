package com.example.pokedex.feature.pokemondetail

import com.example.pokedex.core.mvi.UiEffect
import com.example.pokedex.core.mvi.UiEvent
import com.example.pokedex.core.mvi.UiState
import com.example.pokedex.core.util.UiText
import com.example.pokedex.domain.model.Pokemon

/** Immutable loading, content, or error state for one Pokémon detail. */
data class PokemonDetailState(
    val isLoading: Boolean = false,
    val pokemon: Pokemon? = null,
    val errorMessage: UiText? = null,
) : UiState

/** User and lifecycle events accepted by [PokemonDetailViewModel]. */
sealed interface PokemonDetailEvent : UiEvent {
    data class LoadPokemon(
        val id: Int,
    ) : PokemonDetailEvent

    object ToggleFavorite : PokemonDetailEvent

    object PlayCry : PokemonDetailEvent
}

/** One-shot presentation actions emitted by [PokemonDetailViewModel]. */
sealed interface PokemonDetailEffect : UiEffect {
    data object ShowPlaybackError : PokemonDetailEffect
}
