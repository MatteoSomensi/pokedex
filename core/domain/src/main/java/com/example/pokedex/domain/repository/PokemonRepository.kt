package com.example.pokedex.domain.repository

import androidx.paging.PagingData
import com.example.pokedex.domain.model.Pokemon
import com.example.pokedex.domain.result.AppResult
import kotlinx.coroutines.flow.Flow

/**
 * Domain boundary for paginated, cached, and user-specific Pokémon data.
 *
 * Implementations prioritize local data where possible and expose operational failures through
 * [AppResult]. Coroutine cancellation must propagate rather than being wrapped as a failure.
 */
interface PokemonRepository {
    /** Streams Room-backed pages matching the normalized [query]. */
    fun getPokemonPaged(query: String = ""): Flow<PagingData<Pokemon>>

    /**
     * Returns one offset-based page, optionally bypassing a sufficient local page.
     *
     * @param limit maximum number of items requested.
     * @param offset zero-based item offset.
     * @param forceRefresh whether to fetch remote data even when local data can satisfy the request.
     */
    suspend fun getPokemonList(
        limit: Int,
        offset: Int,
        forceRefresh: Boolean = false,
    ): AppResult<List<Pokemon>>

    /** Returns a locally cached detail or fetches and stores it when absent. */
    suspend fun getPokemonDetail(id: Int): AppResult<Pokemon>

    /** Searches by partial name or exact numeric ID using offset-based pagination. */
    suspend fun searchPokemon(
        query: String,
        limit: Int,
        offset: Int,
    ): AppResult<List<Pokemon>>

    /** Returns display-ready Pokémon type names, excluding unsupported special types. */
    suspend fun getPokemonTypes(): AppResult<List<String>>

    /** Persists the desired favorite state, fetching the Pokémon first when it is not cached. */
    suspend fun toggleFavoriteStatus(
        id: Int,
        isFavorite: Boolean,
    ): AppResult<Unit>

    /** Returns all locally stored favorite Pokémon ordered by ID. */
    suspend fun getFavoritePokemonList(): AppResult<List<Pokemon>>

    /** Observes the set of IDs currently marked as favorites. */
    fun observeFavoritePokemonIds(): Flow<Set<Int>>
}
