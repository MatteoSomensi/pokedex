package com.example.pokedex.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pokedex.data.local.dao.PokemonDao
import com.example.pokedex.data.local.dao.RemoteKeyDao
import com.example.pokedex.data.local.entity.PokemonEntity
import com.example.pokedex.data.local.entity.PokemonRemoteKey
import com.example.pokedex.data.local.entity.TypeEntity

/**
 * Room database that is the app's local source of truth for Pokémon, types, and paging keys.
 *
 * Schema migrations are registered by the data-layer Hilt module. Consumers should use the DAOs
 * instead of issuing raw database operations.
 */
@Database(
    entities = [PokemonEntity::class, TypeEntity::class, PokemonRemoteKey::class],
    version = 4,
    exportSchema = false,
)
abstract class PokedexDatabase : RoomDatabase() {
    /** Provides access to cached Pokémon, favorites, and types. */
    abstract fun pokemonDao(): PokemonDao

    /** Provides access to query-scoped keys used by Paging 3. */
    abstract fun remoteKeyDao(): RemoteKeyDao

    companion object {
        const val DATABASE_NAME = "pokedex_db"
    }
}
