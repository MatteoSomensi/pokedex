package com.example.pokedex.domain.model

/**
 * Domain representation of a Pokémon shared by repositories and presentation modules.
 *
 * PokeAPI expresses [height] in decimetres and [weight] in hectograms. Use [heightInMeters] and
 * [weightInKg] when presenting converted values.
 *
 * @property stats base statistic values keyed by their PokeAPI names.
 * @property isFavorite local user preference preserved across remote refreshes.
 */
data class Pokemon(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val cryUrl: String,
    val types: List<String>,
    val height: Int = 0,
    val weight: Int = 0,
    val stats: Map<String, Int> = emptyMap(),
    val isFavorite: Boolean = false,
)

/** Height converted from PokeAPI decimetres to metres. */
val Pokemon.heightInMeters: Float
    get() = height / 10f

/** Weight converted from PokeAPI hectograms to kilograms. */
val Pokemon.weightInKg: Float
    get() = weight / 10f
