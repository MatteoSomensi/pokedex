package com.example.pokedex.data.repository

import com.example.pokedex.data.remote.PokeApiService
import com.example.pokedex.domain.model.Pokemon
import com.example.pokedex.domain.repository.PokemonRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject


/**
 * This class is responsible for PokemonRepositoryImpl logic.
 * Part of the Clean Architecture structure.
 */
class PokemonRepositoryImpl @Inject constructor(
    private val api: PokeApiService
) : PokemonRepository {

    private val cache = java.util.concurrent.ConcurrentHashMap<String, Pokemon>()

    override suspend fun getPokemonList(limit: Int, offset: Int): Result<List<Pokemon>> = runCatching {
        val listResponse = api.getPokemonList(limit = limit, offset = offset)
        
        val result = mutableListOf<Pokemon>()
        listResponse.results.chunked(5).forEach { chunk ->
            coroutineScope {
                val chunkResults = chunk.map { resultItem ->
                    async {
                        cache[resultItem.name] ?: run {
                            val detail = api.getPokemonDetail(resultItem.name)
                            val pokemon = Pokemon(
                                id = detail.id,
                                name = detail.name,
                                imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${detail.id}.png",
                                types = detail.types.map { it.type.name.replaceFirstChar { char -> char.uppercase() } },
                                height = detail.height,
                                weight = detail.weight,
                                stats = detail.stats.associate { it.stat.name to it.baseStat }
                            )
                            cache[resultItem.name] = pokemon
                            pokemon
                        }
                    }
                }.awaitAll()
                result.addAll(chunkResults)
            }
        }
        result
    }

    override suspend fun getPokemonDetail(id: Int): Result<Pokemon> = runCatching {
        cache.values.find { it.id == id } ?: run {
            val detail = api.getPokemonDetail(id.toString())
            val pokemon = Pokemon(
                id = detail.id,
                name = detail.name,
                imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${detail.id}.png",
                types = detail.types.map { it.type.name.replaceFirstChar { char -> char.uppercase() } },
                height = detail.height,
                weight = detail.weight,
                stats = detail.stats.associate { it.stat.name to it.baseStat }
            )
            cache[detail.name] = pokemon
            pokemon
        }
    }

    private var globalListCache: List<com.example.pokedex.data.remote.model.PokemonResultItem>? = null

    override suspend fun searchPokemon(query: String, limit: Int, offset: Int): Result<List<Pokemon>> = runCatching {
        if (globalListCache == null) {
            val fullList = api.getPokemonList(limit = 10000, offset = 0)
            globalListCache = fullList.results
        }
        val q = query.trim().lowercase()
        val filtered = globalListCache!!.filter {
            it.name.lowercase().contains(q) || it.url.trimEnd('/').substringAfterLast('/') == q
        }
        val chunk = filtered.drop(offset).take(limit)
        
        val result = mutableListOf<Pokemon>()
        chunk.chunked(5).forEach { c ->
            coroutineScope {
                val chunkResults = c.map { resultItem ->
                    async {
                        cache[resultItem.name] ?: run {
                            val detail = api.getPokemonDetail(resultItem.name)
                            val pokemon = Pokemon(
                                id = detail.id,
                                name = detail.name,
                                imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${detail.id}.png",
                                types = detail.types.map { it.type.name.replaceFirstChar { char -> char.uppercase() } },
                                height = detail.height,
                                weight = detail.weight,
                                stats = detail.stats.associate { it.stat.name to it.baseStat }
                            )
                            cache[resultItem.name] = pokemon
                            pokemon
                        }
                    }
                }.awaitAll()
                result.addAll(chunkResults)
            }
        }
        result
    }
}
