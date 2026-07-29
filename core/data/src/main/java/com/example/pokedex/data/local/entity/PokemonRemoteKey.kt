package com.example.pokedex.data.local.entity

import androidx.room.Entity

/**
 * Query-specific Paging 3 cursor for a cached Pokémon.
 *
 * Including [query] in the primary key keeps the unfiltered list and every search result set
 * independent even though they share the same Pokémon table.
 */
@Entity(
    tableName = "pokemon_remote_keys",
    primaryKeys = ["pokemonId", "query"],
)
data class PokemonRemoteKey(
    val pokemonId: Int,
    val query: String,
    val prevOffset: Int?,
    val nextOffset: Int?,
)
