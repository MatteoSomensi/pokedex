package com.example.pokedex.domain.repository

import com.example.pokedex.domain.model.Pokemon


/**
 * Repository interface defining operations for accessing Pokemon data.
 * Implements an offline-first strategy where local data is prioritized, 
 * falling back to remote fetching when necessary.
 */
interface PokemonRepository {
    fun getPokemonPaged(query: String = ""): kotlinx.coroutines.flow.Flow<androidx.paging.PagingData<Pokemon>>
    suspend fun getPokemonList(limit: Int, offset: Int, forceRefresh: Boolean = false): Result<List<Pokemon>>
    suspend fun getPokemonDetail(id: Int): Result<Pokemon>
    suspend fun searchPokemon(query: String, limit: Int, offset: Int): Result<List<Pokemon>>
    suspend fun getPokemonTypes(): Result<List<String>>
    suspend fun toggleFavoriteStatus(id: Int, isFavorite: Boolean): Result<Unit>
    suspend fun getFavoritePokemonList(): Result<List<Pokemon>>
    fun observeFavoritePokemonIds(): kotlinx.coroutines.flow.Flow<Set<Int>>
}
