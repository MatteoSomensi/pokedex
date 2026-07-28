package com.example.pokedex.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pokedex.data.local.dao.PokemonDao
import com.example.pokedex.data.local.dao.RemoteKeyDao
import com.example.pokedex.data.local.entity.PokemonEntity
import com.example.pokedex.data.local.entity.PokemonRemoteKey
import com.example.pokedex.data.local.entity.TypeEntity

@Database(
    entities = [PokemonEntity::class, TypeEntity::class, PokemonRemoteKey::class],
    version = 4,
    exportSchema = false,
)
abstract class PokedexDatabase : RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao
    abstract fun remoteKeyDao(): RemoteKeyDao

    companion object {
        const val DATABASE_NAME = "pokedex_db"
    }
}
