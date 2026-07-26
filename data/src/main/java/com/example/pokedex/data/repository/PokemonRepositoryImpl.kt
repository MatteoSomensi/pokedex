package com.example.pokedex.data.repository

import com.example.pokedex.core.util.Constants
import com.example.pokedex.data.remote.PokeApiService
import com.example.pokedex.data.remote.model.PokemonResultItem
import com.example.pokedex.domain.model.Pokemon
import com.example.pokedex.domain.repository.PokemonRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

/**
 * This class is responsible for PokemonRepositoryImpl logic.
 * Part of the Clean Architecture structure.
 */
class PokemonRepositoryImpl @Inject constructor(
    private val api: PokeApiService,
    private val dao: com.example.pokedex.data.local.dao.PokemonDao
) : PokemonRepository {

    private var globalListCache: List<PokemonResultItem>? = null
    private var cachedTypes: List<String>? = null

    override suspend fun getPokemonList(limit: Int, offset: Int): Result<List<Pokemon>> = runCatching {
        // 1. Try to load from local database first
        val localList = dao.getPokemonList(limit, offset)
        if (localList.isNotEmpty() && localList.size == limit) {
            return@runCatching localList.map { it.toDomain() }
        }

        // 2. If not enough data locally, fetch from network
        val listResponse = api.getPokemonList(limit = limit, offset = offset)
        
        val chunkResults = coroutineScope {
            listResponse.results.map { resultItem ->
                async {
                    val detail = api.getPokemonDetail(resultItem.name)
                    val pokemon = Pokemon(
                        id = detail.id,
                        name = detail.name,
                        imageUrl = "${Constants.POKE_IMAGE_BASE_URL}${detail.id}.png",
                        types = detail.types.map { it.type.name.replaceFirstChar { char -> char.uppercase() } },
                        height = detail.height,
                        weight = detail.weight,
                        stats = detail.stats.associate { it.stat.name to it.baseStat }
                    )
                    pokemon
                }
            }.awaitAll()
        }

        // 3. Save to local database
        dao.insertAll(chunkResults.map { com.example.pokedex.data.local.entity.PokemonEntity.fromDomain(it) })

        chunkResults
    }

    override suspend fun getPokemonDetail(id: Int): Result<Pokemon> = runCatching {
        val local = dao.getPokemonById(id)
        if (local != null) {
            return@runCatching local.toDomain()
        }

        val detail = api.getPokemonDetail(id.toString())
        val pokemon = Pokemon(
            id = detail.id,
            name = detail.name,
            imageUrl = "${Constants.POKE_IMAGE_BASE_URL}${detail.id}.png",
            types = detail.types.map { it.type.name.replaceFirstChar { char -> char.uppercase() } },
            height = detail.height,
            weight = detail.weight,
            stats = detail.stats.associate { it.stat.name to it.baseStat }
        )
        dao.insert(com.example.pokedex.data.local.entity.PokemonEntity.fromDomain(pokemon))
        pokemon
    }

    override suspend fun getPokemonTypes(): Result<List<String>> = runCatching {
        cachedTypes?.let { return@runCatching it }
        val response = api.getPokemonTypes()
        val types = response.results.map { it.name.replaceFirstChar { char -> char.uppercase() } }
            .filter { it != "Unknown" && it != "Shadow" }
        cachedTypes = types
        types
    }

    override suspend fun searchPokemon(
        query: String,
        limit: Int,
        offset: Int
    ): Result<List<Pokemon>> = runCatching {
        val q = query.trim().lowercase()
        val queryId = q.toIntOrNull()
        
        // Local search first
        val localResults = dao.searchPokemon(q, queryId, limit, offset)
        if (localResults.isNotEmpty()) {
             // For search we might not have all results locally, but offline-first means 
             // we rely on local DB. If we want full search, we might need a network sync of the full index.
             // To keep it simple, we just return local results if we have them. 
             // Ideally we'd fetch from network if empty, but search pagination is tricky with partial caches.
             return@runCatching localResults.map { it.toDomain() }
        }

        if (globalListCache == null) {
            val fullList = api.getPokemonList(limit = MAX_POKEMON_LIMIT, offset = 0)
            globalListCache = fullList.results
        }
        
        val filtered = globalListCache!!.filter {
            it.name.lowercase().contains(q) || it.url.trimEnd('/').substringAfterLast('/') == q
        }
        val chunk = filtered.drop(offset).take(limit)

        val chunkResults = coroutineScope {
            chunk.map { resultItem ->
                async {
                    val detail = api.getPokemonDetail(resultItem.name)
                    Pokemon(
                        id = detail.id,
                        name = detail.name,
                        imageUrl = "${Constants.POKE_IMAGE_BASE_URL}${detail.id}.png",
                        types = detail.types.map { it.type.name.replaceFirstChar { char -> char.uppercase() } },
                        height = detail.height,
                        weight = detail.weight,
                        stats = detail.stats.associate { it.stat.name to it.baseStat }
                    )
                }
            }.awaitAll()
        }
        
        dao.insertAll(chunkResults.map { com.example.pokedex.data.local.entity.PokemonEntity.fromDomain(it) })
        chunkResults
    }

    companion object {
        private const val MAX_POKEMON_LIMIT = 10000
    }
}
