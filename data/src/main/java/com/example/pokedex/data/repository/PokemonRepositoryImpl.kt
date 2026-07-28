package com.example.pokedex.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.pokedex.core.coroutines.DispatcherProvider
import com.example.pokedex.core.util.Constants
import com.example.pokedex.data.local.PokedexDatabase
import com.example.pokedex.data.local.dao.PokemonDao
import com.example.pokedex.data.local.entity.PokemonEntity
import com.example.pokedex.data.local.entity.TypeEntity
import com.example.pokedex.data.remote.PokeApiService
import com.example.pokedex.data.remote.model.PokemonDetailResponse
import com.example.pokedex.data.remote.model.PokemonResultItem
import com.example.pokedex.data.repository.paging.PokemonRemoteMediator
import com.example.pokedex.domain.model.Pokemon
import com.example.pokedex.domain.repository.PokemonRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Offline-first implementation of [PokemonRepository].
 *
 * Room is the source read by Paging, while [PokemonRemoteMediator] fetches network pages and writes
 * Pokémon plus query-scoped remote keys in one transaction. Direct reads prefer cached data and
 * populate missing records from PokeAPI. Favorite state is always preserved when network data
 * replaces a cached record.
 *
 * Network and database work runs on [DispatcherProvider.io]. Cancellation is rethrown rather than
 * converted to a failed [Result].
 */
class PokemonRepositoryImpl
    @Inject
    constructor(
        private val api: PokeApiService,
        private val db: PokedexDatabase,
        private val dao: PokemonDao,
        private val dispatchers: DispatcherProvider,
    ) : PokemonRepository {
        private var globalListCache: List<PokemonResultItem>? = null
        private var cachedTypes: List<String>? = null
        private val listMutex = Mutex()
        private val typesMutex = Mutex()

        @Volatile
        private var isEndReached = false

        @OptIn(ExperimentalPagingApi::class)
        override fun getPokemonPaged(query: String): Flow<PagingData<Pokemon>> {
            val normalizedQuery = query.trim().lowercase()

            return Pager(
                config =
                    PagingConfig(
                        pageSize = 20,
                        enablePlaceholders = false,
                        initialLoadSize = 40,
                    ),
                remoteMediator =
                    PokemonRemoteMediator(
                        api = api,
                        db = db,
                        query = normalizedQuery,
                        fetchAllPokemon = {
                            listMutex.withLock {
                                if (globalListCache == null) {
                                    globalListCache = api.getPokemonList(limit = MAX_POKEMON_LIMIT, offset = 0).results
                                }
                                globalListCache!!
                            }
                        },
                    ),
                pagingSourceFactory = { dao.getPokemonPagingSource(normalizedQuery) },
            ).flow.map { pagingData ->
                pagingData.map { it.toDomain() }
            }
        }

        override suspend fun getPokemonList(
            limit: Int,
            offset: Int,
            forceRefresh: Boolean,
        ): Result<List<Pokemon>> =
            withContext(dispatchers.io) {
                runCatching {
                    if (offset == 0) isEndReached = false

                    val localList = dao.getPokemonList(limit, offset)
                    if (!forceRefresh && (localList.size == limit || (localList.isNotEmpty() && isEndReached))) {
                        return@runCatching localList.map { it.toDomain() }
                    }

                    val listResponse = api.getPokemonList(limit = limit, offset = offset)
                    if (listResponse.results.size < limit) {
                        isEndReached = true
                    }

                    val chunkResults = mutableListOf<Pokemon>()
                    listResponse.results.chunked(10).forEach { chunk ->
                        val partialResults =
                            coroutineScope {
                                chunk
                                    .map { resultItem ->
                                        async {
                                            val networkDomain = api.getPokemonDetail(resultItem.name).toDomain()
                                            val localEntity = dao.getPokemonById(networkDomain.id)
                                            if (localEntity != null) {
                                                networkDomain.copy(isFavorite = localEntity.isFavorite)
                                            } else {
                                                networkDomain
                                            }
                                        }
                                    }.awaitAll()
                            }
                        chunkResults.addAll(partialResults)
                    }

                    dao.insertAll(chunkResults.map { PokemonEntity.fromDomain(it) })

                    chunkResults
                }.onFailure { if (it is CancellationException) throw it }
            }

        override suspend fun getPokemonDetail(id: Int): Result<Pokemon> =
            withContext(dispatchers.io) {
                runCatching {
                    val local = dao.getPokemonById(id)
                    if (local != null) {
                        return@runCatching local.toDomain()
                    }

                    val detail = api.getPokemonDetail(id.toString())
                    val pokemon = detail.toDomain()
                    dao.insert(PokemonEntity.fromDomain(pokemon))
                    pokemon
                }.onFailure { if (it is CancellationException) throw it }
            }

        override suspend fun getPokemonTypes(): Result<List<String>> =
            withContext(dispatchers.io) {
                runCatching {
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
                            response.results
                                .map { it.name.replaceFirstChar { char -> char.uppercase() } }
                                .filter { it != "Unknown" && it != "Shadow" }

                        dao.insertTypes(types.map { TypeEntity(it) })
                        cachedTypes = types
                        types
                    }
                }.onFailure { if (it is CancellationException) throw it }
            }

        override suspend fun searchPokemon(
            query: String,
            limit: Int,
            offset: Int,
        ): Result<List<Pokemon>> =
            withContext(dispatchers.io) {
                runCatching {
                    val q = query.trim().lowercase()

                    var useLocalOnly = false
                    listMutex.withLock {
                        if (globalListCache == null) {
                            try {
                                val fullList = api.getPokemonList(limit = MAX_POKEMON_LIMIT, offset = 0)
                                globalListCache = fullList.results
                            } catch (e: Exception) {
                                if (e is CancellationException) throw e
                                useLocalOnly = true
                            }
                        }
                    }

                    if (useLocalOnly) {
                        val localResults = dao.searchPokemon(q, limit, offset)
                        return@runCatching localResults.map { it.toDomain() }
                    }

                    val filtered =
                        globalListCache!!.filter {
                            it.name.lowercase().contains(q) || it.url.trimEnd('/').substringAfterLast('/') == q
                        }
                    val chunk = filtered.drop(offset).take(limit)

                    val chunkResults = mutableListOf<Pokemon>()
                    chunk.chunked(10).forEach { batch ->
                        val partialResults =
                            coroutineScope {
                                batch
                                    .map { resultItem ->
                                        async {
                                            val pokemonId =
                                                resultItem.url
                                                    .trimEnd('/')
                                                    .substringAfterLast('/')
                                                    .toIntOrNull()
                                                    ?: -1
                                            val localDetail = dao.getPokemonById(pokemonId)
                                            if (localDetail != null) {
                                                localDetail.toDomain()
                                            } else {
                                                try {
                                                    api.getPokemonDetail(resultItem.name).toDomain()
                                                } catch (e: Exception) {
                                                    if (e is CancellationException) throw e
                                                    null
                                                }
                                            }
                                        }
                                    }.awaitAll()
                                    .filterNotNull()
                            }
                        chunkResults.addAll(partialResults)
                    }

                    dao.insertAll(chunkResults.map { PokemonEntity.fromDomain(it) })
                    chunkResults
                }.onFailure { if (it is CancellationException) throw it }
            }

        private fun PokemonDetailResponse.toDomain(): Pokemon =
            Pokemon(
                id = id,
                name = name,
                imageUrl = "${Constants.POKE_IMAGE_BASE_URL}$id.png",
                cryUrl = "https://raw.githubusercontent.com/PokeAPI/cries/main/cries/pokemon/latest/$id.ogg",
                types = types.map { it.type.name.replaceFirstChar { char -> char.uppercase() } },
                height = height,
                weight = weight,
                stats = stats.associate { it.stat.name to it.baseStat },
            )

        override suspend fun toggleFavoriteStatus(
            id: Int,
            isFavorite: Boolean,
        ): Result<Unit> =
            withContext(dispatchers.io) {
                runCatching {
                    val affected = dao.updateFavoriteStatus(id, isFavorite)
                    if (affected == 0) {
                        val detail =
                            api.getPokemonDetail(id.toString()).toDomain().copy(isFavorite = isFavorite)
                        dao.insert(PokemonEntity.fromDomain(detail))
                    }
                }.onFailure { if (it is CancellationException) throw it }
            }

        override suspend fun getFavoritePokemonList(): Result<List<Pokemon>> =
            withContext(dispatchers.io) {
                runCatching {
                    dao.getFavoritePokemonList().map { it.toDomain() }
                }.onFailure { if (it is CancellationException) throw it }
            }

        override fun observeFavoritePokemonIds(): Flow<Set<Int>> = dao.observeFavoritePokemonIds().map { it.toSet() }

        companion object {
            private const val MAX_POKEMON_LIMIT = 1500
        }
    }
