package com.example.pokedex.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pokedex.data.local.entity.PokemonRemoteKey

@Dao
@JvmSuppressWildcards
interface RemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<PokemonRemoteKey>): List<Long>

    @Query("SELECT * FROM pokemon_remote_keys WHERE pokemonId = :pokemonId AND `query` = :query")
    suspend fun remoteKey(
        pokemonId: Int,
        query: String,
    ): PokemonRemoteKey?

    @Query("DELETE FROM pokemon_remote_keys WHERE `query` = :query")
    suspend fun clearRemoteKeys(query: String): Int
}
