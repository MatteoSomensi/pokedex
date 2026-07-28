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
import com.example.pokedex.data.remote.model.PokemonResultItem
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * Coordinates PokeAPI pages with the Room-backed Paging 3 source.
 *
 * An empty query loads the remote collection directly. A non-empty query filters a shared catalog
 * supplied by [fetchAllPokemon], then fetches details only for the requested page. Pokémon records
 * and [PokemonRemoteKey] values are committed in the same Room transaction, and existing favorite
 * flags survive refreshes.
 *
 * @param query Search text normalized independently for each pager.
 * @param fetchAllPokemon Lazily supplies the complete lightweight catalog used for remote search.
 */
@OptIn(ExperimentalPagingApi::class)
class PokemonRemoteMediator(
    private val api: PokeApiService,
    private val db: PokedexDatabase,
    query: String = "",
    private val fetchAllPokemon: suspend () -> List<PokemonResultItem>,
) : RemoteMediator<Int, PokemonEntity>() {
    private val normalizedQuery = query.trim().lowercase()

    override suspend fun initialize(): InitializeAction = InitializeAction.LAUNCH_INITIAL_REFRESH

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PokemonEntity>,
    ): MediatorResult {
        return try {
            val offset =
                when (loadType) {
                    LoadType.REFRESH -> 0
                    LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                    LoadType.APPEND -> {
                        val remoteKey = getRemoteKeyForLastItem(state)
                        remoteKey?.nextOffset
                            ?: return MediatorResult.Success(
                                endOfPaginationReached = true,
                            )
                    }
                }
            val limit =
                when (loadType) {
                    LoadType.REFRESH -> state.config.initialLoadSize
                    else -> state.config.pageSize
                }
            val pokemonItems = loadPokemonItems(offset = offset, limit = limit)
            val detailedPokemon =
                coroutineScope {
                    pokemonItems
                        .map { item ->
                            async { api.getPokemonDetail(item.name) }
                        }.awaitAll()
                }
            val endOfPaginationReached = pokemonItems.size < limit

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.remoteKeyDao().clearRemoteKeys(normalizedQuery)
                }

                val prevOffset = (offset - limit).takeIf { it >= 0 }
                val nextOffset =
                    (offset + pokemonItems.size)
                        .takeUnless { endOfPaginationReached }
                db.remoteKeyDao().insertAll(
                    detailedPokemon.map { pokemon ->
                        PokemonRemoteKey(
                            pokemonId = pokemon.id,
                            query = normalizedQuery,
                            prevOffset = prevOffset,
                            nextOffset = nextOffset,
                        )
                    },
                )

                val ids = detailedPokemon.map { it.id }
                val favorites =
                    if (ids.isEmpty()) {
                        emptySet()
                    } else {
                        db
                            .pokemonDao()
                            .getPokemonByIds(ids)
                            .filter { it.isFavorite }
                            .map { it.id }
                            .toSet()
                    }

                val entities =
                    detailedPokemon.map { response ->
                        val entity = response.toLocalEntity()
                        entity.copy(isFavorite = favorites.contains(entity.id))
                    }
                db.pokemonDao().insertAll(entities)
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            MediatorResult.Error(exception)
        }
    }

    private suspend fun loadPokemonItems(
        offset: Int,
        limit: Int,
    ): List<PokemonResultItem> {
        if (normalizedQuery.isBlank()) {
            return api.getPokemonList(limit = limit, offset = offset).results
        }

        val allPokemon = fetchAllPokemon()
        return allPokemon
            .asSequence()
            .filter { item ->
                item.name.lowercase().contains(normalizedQuery) ||
                    item.idFromUrl() == normalizedQuery
            }.drop(offset)
            .take(limit)
            .toList()
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, PokemonEntity>): PokemonRemoteKey? {
        val lastItem =
            state.pages
                .lastOrNull { it.data.isNotEmpty() }
                ?.data
                ?.lastOrNull()
        return lastItem?.let { pokemon ->
            db.remoteKeyDao().remoteKey(pokemon.id, normalizedQuery)
        }
    }

    private fun PokemonResultItem.idFromUrl(): String = url.trimEnd('/').substringAfterLast('/')

    private fun PokemonDetailResponse.toLocalEntity(): PokemonEntity =
        PokemonEntity(
            id = id,
            name = name,
            imageUrl = "${Constants.POKE_IMAGE_BASE_URL}$id.png",
            types =
                types.joinToString(",") {
                    it.type.name.replaceFirstChar(Char::uppercase)
                },
            height = height,
            weight = weight,
            stats = stats.joinToString(";") { "${it.stat.name}:${it.baseStat}" },
            isFavorite = false,
        )
}
