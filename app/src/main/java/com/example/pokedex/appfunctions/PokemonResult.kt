package com.example.pokedex.appfunctions

import androidx.appfunctions.AppFunctionSerializable

/**
 * A Pokemon returned by the AppFunctions.
 */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class PokemonResult(
    /** The unique numeric ID of the Pokemon. */
    val id: Int,
    /** The name of the Pokemon. */
    val name: String,
    /** Whether the Pokemon is a favorite. */
    val isFavorite: Boolean,
)
