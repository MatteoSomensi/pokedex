package com.example.pokedex.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Cached Pokémon type used to serve filter choices without a network request. */
@Entity(tableName = "types")
data class TypeEntity(
    @PrimaryKey
    val name: String,
)
