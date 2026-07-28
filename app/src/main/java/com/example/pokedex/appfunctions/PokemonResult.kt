package com.example.pokedex.appfunctions

import androidx.appfunctions.AppFunctionSerializable

/** Compact, serializable Pokémon projection returned to AppFunctions callers. */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class PokemonResult(
    /** Unique numeric PokeAPI identifier. */
    val id: Int,
    /** Canonical Pokémon name. */
    val name: String,
    /** Whether the user currently marks the Pokémon as a favorite. */
    val isFavorite: Boolean,
)
