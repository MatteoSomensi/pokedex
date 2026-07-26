package com.example.pokedex.data.repository

import com.example.pokedex.core.util.Constants
import com.example.pokedex.data.local.dao.PokemonDao
import com.example.pokedex.data.local.entity.PokemonEntity
import com.example.pokedex.data.local.entity.TypeEntity
import com.example.pokedex.data.remote.PokeApiService
import com.example.pokedex.data.remote.model.PokemonResultItem
import com.example.pokedex.data.remote.model.PokemonDetailResponse
import com.example.pokedex.domain.model.Pokemon
import com.example.pokedex.domain.repository.PokemonRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

/**
 * Concrete implementation of [PokemonRepository].
 * This repository implements an offline-first strategy:
 * 1. It attempts to load data from the local Room database ([dao]).
 * 2. If the data is missing or incomplete, it fetches it from the network via [api].
 * 3. Network responses are then cached locally for subsequent requests.
 * 
 * Also handles concurrency (using Mutex) to prevent race conditions during cache updates.
 */
class PokemonRepositoryImpl @Inject constructor(
    private val api: PokeApiService,
    private val dao: PokemonDao
) : PokemonRepository {

    private var globalListCache: List<PokemonResultItem>? = null
    private var cachedTypes: List<String>? = null
    private val listMutex = Mutex()
    private val typesMutex = Mutex()
    @Volatile private var isEndReached = false

    override suspend fun getPokemonList(limit: Int, offset: Int): Result<List<Pokemon>> =
        runCatching {
            if (offset == 0) isEndReached = false
            
            val localList = dao.getPokemonList(limit, offset)
            if (localList.size == limit || (localList.isNotEmpty() && isEndReached)) {
                return@runCatching localList.map { it.toDomain() }
            }

            val listResponse = api.getPokemonList(limit = limit, offset = offset)
            if (listResponse.results.size < limit) {
                isEndReached = true
            }

            val chunkResults = mutableListOf<Pokemon>()
            listResponse.results.chunked(10).forEach { chunk ->
                val partialResults = coroutineScope {
                    chunk.map { resultItem ->
                        async {
                            api.getPokemonDetail(resultItem.name).toDomain()
                        }
                    }.awaitAll()
                }
                chunkResults.addAll(partialResults)
            }

            dao.insertAll(chunkResults.map { PokemonEntity.fromDomain(it) })

            chunkResults
        }.onFailure { if (it is kotlinx.coroutines.CancellationException) throw it }

    override suspend fun getPokemonDetail(id: Int): Result<Pokemon> = runCatching {
        val local = dao.getPokemonById(id)
        if (local != null) {
            return@runCatching local.toDomain()
        }

        val detail = api.getPokemonDetail(id.toString())
        val pokemon = detail.toDomain()
        dao.insert(PokemonEntity.fromDomain(pokemon))
        pokemon
    }.onFailure { if (it is kotlinx.coroutines.CancellationException) throw it }

    override suspend fun getPokemonTypes(): Result<List<String>> = runCatching {
        typesMutex.withLock {
            cachedTypes?.let { return@withLock it }

            val localTypes = dao.getTypes()
            if (localTypes.isNotEmpty()) {
                val types = localTypes.map { it.name }
                cachedTypes = types
                return@withLock types
            }

            val response = api.getPokemonTypes()
            val types =
                response.results.map { it.name.replaceFirstChar { char -> char.uppercase() } }
                    .filter { it != "Unknown" && it != "Shadow" }

            dao.insertTypes(types.map { TypeEntity(it) })
            cachedTypes = types
            types
        }
    }.onFailure { if (it is kotlinx.coroutines.CancellationException) throw it }

    override suspend fun searchPokemon(
        query: String,
        limit: Int,
        offset: Int
    ): Result<List<Pokemon>> = runCatching {
        val q = query.trim().lowercase()
        val queryId = q.toIntOrNull()

        listMutex.withLock {
            if (globalListCache == null) {
                val fullList = api.getPokemonList(limit = MAX_POKEMON_LIMIT, offset = 0)
                globalListCache = fullList.results
            }
        }

        val filtered = globalListCache!!.filter {
            it.name.lowercase().contains(q) || it.url.trimEnd('/').substringAfterLast('/') == q
        }
        val chunk = filtered.drop(offset).take(limit)

        val chunkResults = mutableListOf<Pokemon>()
        chunk.chunked(10).forEach { batch ->
            val partialResults = coroutineScope {
                batch.map { resultItem ->
                    async {
                        val pokemonId =
                            resultItem.url.trimEnd('/').substringAfterLast('/').toIntOrNull() ?: -1
                        val localDetail = dao.getPokemonById(pokemonId)
                        if (localDetail != null) {
                            localDetail.toDomain()
                        } else {
                            api.getPokemonDetail(resultItem.name).toDomain()
                        }
                    }
                }.awaitAll()
            }
            chunkResults.addAll(partialResults)
        }

        dao.insertAll(chunkResults.map { PokemonEntity.fromDomain(it) })
        chunkResults
    }.onFailure { if (it is kotlinx.coroutines.CancellationException) throw it }

    private fun PokemonDetailResponse.toDomain(): Pokemon {
        return Pokemon(
            id = id,
            name = name,
            imageUrl = "${Constants.POKE_IMAGE_BASE_URL}${id}.png",
            types = types.map { it.type.name.replaceFirstChar { char -> char.uppercase() } },
            height = height,
            weight = weight,
            stats = stats.associate { it.stat.name to it.baseStat }
        )
    }

    override suspend fun toggleFavoriteStatus(id: Int, isFavorite: Boolean): Result<Unit> = runCatching {
        dao.updateFavoriteStatus(id, isFavorite)
        Unit
    }.onFailure { if (it is kotlinx.coroutines.CancellationException) throw it }

    override suspend fun getFavoritePokemonList(): Result<List<Pokemon>> = runCatching {
        dao.getFavoritePokemonList().map { it.toDomain() }
    }.onFailure { if (it is kotlinx.coroutines.CancellationException) throw it }

    override fun observeFavoritePokemonIds(): kotlinx.coroutines.flow.Flow<Set<Int>> {
        return dao.observeFavoritePokemonIds().map { it.toSet() }
    }

    companion object {
        private const val MAX_POKEMON_LIMIT = 1500
    }
}
