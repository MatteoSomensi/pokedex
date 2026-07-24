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
}
