package com.example.pokedex.domain.model

/**
 * This class is responsible for Pokemon logic.
 * Part of the Clean Architecture structure.
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

val Pokemon.heightInMeters: Float
    get() = height / 10f

val Pokemon.weightInKg: Float
    get() = weight / 10f
