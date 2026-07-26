package com.example.pokedex.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pokedex.data.local.dao.PokemonDao
import com.example.pokedex.data.local.entity.PokemonEntity

@Database(entities = [PokemonEntity::class], version = 1, exportSchema = false)
abstract class PokedexDatabase : RoomDatabase() {
    abstract val pokemonDao: PokemonDao

    companion object {
        const val DATABASE_NAME = "pokedex_db"
    }
}
