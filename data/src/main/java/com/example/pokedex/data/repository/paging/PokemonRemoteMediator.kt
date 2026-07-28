package com.example.pokedex.data.repository.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.pokedex.core.util.Constants
import com.example.pokedex.data.local.PokedexDatabase
import com.example.pokedex.data.local.entity.PokemonEntity
import com.example.pokedex.data.local.entity.PokemonRemoteKey
import com.example.pokedex.data.remote.PokeApiService
import com.example.pokedex.data.remote.model.PokemonDetailResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

@OptIn(ExperimentalPagingApi::class)
class PokemonRemoteMediator(
    private val api: PokeApiService,
    private val db: PokedexDatabase,
    private val query: String = ""
) : RemoteMediator<Int, PokemonEntity>() {

    override suspend fun initialize(): RemoteMediator.InitializeAction {
        return RemoteMediator.InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PokemonEntity>
    ): MediatorResult {
        return try {
            val offset = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextOffset = remoteKeys?.nextOffset
                        ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    nextOffset
                }
            }

            val limit = state.config.pageSize

            val isSearch = query.isNotBlank()
            
            // In a real Pokedex API search is not paginated optimally by offset/limit if filtering locally.
            // But we will use the standard getPokemonList for empty query.
            val apiResponse = if (isSearch) {
                // To keep it simple, we don't paginate remote searches (PokeAPI doesn't support query natively).
                // If it's a search, we just return endOfPaginationReached.
                return MediatorResult.Success(endOfPaginationReached = true)
            } else {
                api.getPokemonList(limit = limit, offset = offset)
            }

            val pokemonDtos = apiResponse.results
            
            val detailedPokemons = coroutineScope {
                pokemonDtos.map { basicDto ->
                    val id = basicDto.url.trimEnd('/').substringAfterLast("/").toInt()
                    async {
                        try {
                            api.getPokemonDetail(id.toString())
                        } catch (e: Exception) {
                            null
                        }
                    }
                }.awaitAll().filterNotNull()
            }

            val endOfPaginationReached = pokemonDtos.isEmpty() || detailedPokemons.size < limit

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.remoteKeyDao().clearRemoteKeys()
                    if (!isSearch) {
                        db.pokemonDao().clearNonFavoritePokemon()
                    }
                }

                val prevOffset = if (offset == 0) null else offset - limit
                val nextOffset = if (endOfPaginationReached) null else offset + limit

                val keys = detailedPokemons.map {
                    PokemonRemoteKey(
                        pokemonId = it.id,
                        prevOffset = prevOffset,
                        nextOffset = nextOffset
                    )
                }
                
                db.remoteKeyDao().insertAll(keys)

                val entities = detailedPokemons.map { it.toLocalEntity() }
                // Preserve favorite status if it exists
                val finalEntities = entities.map { newEntity ->
                    val existing = db.pokemonDao().getPokemonById(newEntity.id)
                    if (existing?.isFavorite == true) {
                        newEntity.copy(isFavorite = true)
                    } else {
                        newEntity
                    }
                }
                db.pokemonDao().insertAll(finalEntities)
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, PokemonEntity>): PokemonRemoteKey? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { pokemon ->
                db.remoteKeyDao().remoteKeysPokemonId(pokemon.id)
            }
    }

    private fun PokemonDetailResponse.toLocalEntity(): PokemonEntity {
        return PokemonEntity(
            id = id,
            name = name,
            imageUrl = "${Constants.POKE_IMAGE_BASE_URL}${id}.png",
            types = types.joinToString(",") { it.type.name.replaceFirstChar { char -> char.uppercase() } },
            height = height,
            weight = weight,
            stats = stats.joinToString(";") { "${it.stat.name}:${it.baseStat}" },
            isFavorite = false
        )
    }
}
