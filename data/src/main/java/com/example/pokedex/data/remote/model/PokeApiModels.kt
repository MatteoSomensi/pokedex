package com.example.pokedex.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable

/**
 * This class is responsible for PokeApiModels logic.
 * Part of the Clean Architecture structure.
 */
data class PokemonListResponse(
    val results: List<PokemonResultItem>
)

@Serializable
data class PokemonResultItem(
    val name: String,
    val url: String
)

@Serializable
data class PokemonDetailResponse(
    val id: Int,
    val name: String,
    val types: List<PokemonTypeSlot>,
    val height: Int = 0,
    val weight: Int = 0,
    val stats: List<PokemonStatSlot> = emptyList()
)

@Serializable
data class PokemonTypeSlot(
    val slot: Int,
    val type: PokemonTypeItem
)

@Serializable
data class PokemonTypeItem(
    val name: String,
    val url: String
)

@Serializable
data class PokemonStatSlot(
    @SerialName("base_stat") val baseStat: Int,
    val stat: PokemonStatItem
)

@Serializable
data class PokemonStatItem(
    val name: String
)
