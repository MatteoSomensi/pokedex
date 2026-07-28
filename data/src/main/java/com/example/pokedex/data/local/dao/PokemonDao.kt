package com.example.pokedex.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pokedex.data.local.entity.PokemonEntity
import com.example.pokedex.data.local.entity.TypeEntity

/**
 * Data Access Object (DAO) for interacting with the local SQLite database via Room.
 * Handles caching and retrieving Pokemon entities and their types to support the offline-first architecture.
 */
@Dao
@JvmSuppressWildcards
interface PokemonDao {

    @Query(
        """
        SELECT pokemon.* FROM pokemon
        INNER JOIN pokemon_remote_keys
            ON pokemon.id = pokemon_remote_keys.pokemonId
        WHERE pokemon_remote_keys.`query` = :query
        ORDER BY pokemon.id ASC
        """
    )
    fun getPokemonPagingSource(query: String): androidx.paging.PagingSource<Int, PokemonEntity>

    @Query("DELETE FROM pokemon WHERE isFavorite = 0")
    suspend fun clearNonFavoritePokemon(): Int

    @Query("SELECT * FROM pokemon ORDER BY id ASC LIMIT :limit OFFSET :offset")
    suspend fun getPokemonList(limit: Int, offset: Int): List<PokemonEntity>

    @Query("SELECT * FROM pokemon WHERE id = :id")
    suspend fun getPokemonById(id: Int): PokemonEntity?

    @Query("SELECT * FROM pokemon WHERE name = :name")
    suspend fun getPokemonByName(name: String): PokemonEntity?

    @Query("SELECT * FROM pokemon WHERE name LIKE '%' || :query || '%' OR CAST(id AS TEXT) = :query LIMIT :limit OFFSET :offset")
    suspend fun searchPokemon(
        query: String,
        limit: Int,
        offset: Int
    ): List<PokemonEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pokemonList: List<PokemonEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pokemon: PokemonEntity): Long

    @Query("SELECT * FROM types")
    suspend fun getTypes(): List<TypeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTypes(types: List<TypeEntity>): List<Long>

    @Query("UPDATE pokemon SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Int, isFavorite: Boolean): Int

    @Query("SELECT * FROM pokemon WHERE isFavorite = 1 ORDER BY id ASC")
    suspend fun getFavoritePokemonList(): List<PokemonEntity>

    @Query("SELECT id FROM pokemon WHERE isFavorite = 1")
    fun observeFavoritePokemonIds(): kotlinx.coroutines.flow.Flow<List<Int>>
}
