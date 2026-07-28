package com.example.pokedex.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon_remote_keys")
data class PokemonRemoteKey(
    @PrimaryKey
    val pokemonId: Int,
    val prevOffset: Int?,
    val nextOffset: Int?
)
