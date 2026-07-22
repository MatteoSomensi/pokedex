package com.example.pokedex.domain.model


/**
 * This class is responsible for Pokemon logic.
 * Part of the Clean Architecture structure.
 */
data class Pokemon(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val types: List<String>,
    val height: Int = 0,
    val weight: Int = 0,
    val stats: Map<String, Int> = emptyMap()
)
