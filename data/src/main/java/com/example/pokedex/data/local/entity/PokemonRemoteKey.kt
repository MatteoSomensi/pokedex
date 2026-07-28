package com.example.pokedex.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "pokemon_remote_keys",
    primaryKeys = ["pokemonId", "query"]
)
data class PokemonRemoteKey(
    val pokemonId: Int,
    val query: String,
    val prevOffset: Int?,
    val nextOffset: Int?
)
