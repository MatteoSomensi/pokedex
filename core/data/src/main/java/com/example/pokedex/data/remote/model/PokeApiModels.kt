package com.example.pokedex.data.remote.model

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Wire representation of a paginated Pokémon endpoint response.
 *
 * These DTOs deliberately mirror PokeAPI field names and remain inside the data layer. Repositories
 * convert them to domain models before exposing them to feature modules.
 */
@Serializable
@InternalSerializationApi
data class PokemonListResponse(
    val results: List<PokemonResultItem>,
)

/** Lightweight Pokémon reference returned by PokeAPI collection endpoints. */
@Serializable
@InternalSerializationApi
data class PokemonResultItem(
    val name: String,
    val url: String,
)

/** Detailed Pokémon payload returned by `pokemon/{nameOrId}`. */
@Serializable
@InternalSerializationApi
data class PokemonDetailResponse(
    val id: Int,
    val name: String,
    val types: List<PokemonTypeSlot>,
    val height: Int = 0,
    val weight: Int = 0,
    val stats: List<PokemonStatSlot> = emptyList(),
)

/** Associates a Pokémon type with its ordering slot in the API payload. */
@Serializable
@InternalSerializationApi
data class PokemonTypeSlot(
    val slot: Int,
    val type: PokemonTypeItem,
)

/** Named PokeAPI resource that describes a Pokémon type. */
@Serializable
@InternalSerializationApi
data class PokemonTypeItem(
    val name: String,
    val url: String,
)

/** Base-stat value paired with the stat metadata returned by PokeAPI. */
@Serializable
@InternalSerializationApi
data class PokemonStatSlot(
    @SerialName("base_stat") val baseStat: Int,
    val stat: PokemonStatItem,
)

/** Named stat metadata embedded in a Pokémon detail response. */
@Serializable
@InternalSerializationApi
data class PokemonStatItem(
    val name: String,
)

/** Wire representation of the PokeAPI type collection. */
@Serializable
@InternalSerializationApi
data class TypeListResponse(
    val results: List<PokemonTypeItem>,
)
