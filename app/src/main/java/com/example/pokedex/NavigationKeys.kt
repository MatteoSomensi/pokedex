package com.example.pokedex

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** Root destination containing the paginated Pokémon list. */
@Serializable
data object PokemonList : NavKey

/**
 * Detail destination for one Pokémon.
 *
 * @property id positive PokeAPI identifier.
 */
@Serializable
data class PokemonDetail(
    val id: Int,
) : NavKey

/** Destination for login and account registration. */
@Serializable
data object Auth : NavKey

/** Destination for the authenticated user's profile and logout action. */
@Serializable
data object Profile : NavKey
