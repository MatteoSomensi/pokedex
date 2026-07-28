package com.example.pokedex.appfunctions

import androidx.appfunctions.AppFunctionContext
import androidx.appfunctions.service.AppFunction
import com.example.pokedex.domain.repository.PokemonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * AppFunctions for the Pokedex app.
 */
class PokedexAppFunctions
    @Inject
    constructor(
        private val pokemonRepository: PokemonRepository,
    ) {
        /**
         * Search for a Pokemon by name.
         * Required workflow: Call this before "toggleFavorite" to obtain valid Pokemon IDs.
         *
         * @param appFunctionContext The execution context.
         * @param query The name or partial name of the Pokemon to search for.
         * @return A list of [PokemonResult] matching the query, or empty if not found.
         */
        @AppFunction(isDescribedByKDoc = true)
        suspend fun searchPokemon(
            appFunctionContext: AppFunctionContext,
            query: String,
        ): List<PokemonResult> =
            withContext(Dispatchers.IO) {
                val result = pokemonRepository.searchPokemon(query.lowercase(), limit = 10, offset = 0)
                result.getOrNull()?.map { p ->
                    PokemonResult(
                        id = p.id,
                        name = p.name,
                        isFavorite = p.isFavorite,
                    )
                } ?: emptyList()
            }

        /**
         * Toggle the favorite status of a Pokemon.
         * Required workflow: Call "searchPokemon" first to obtain valid Pokemon IDs.
         *
         * @param appFunctionContext The execution context.
         * @param pokemonId The numeric ID of the Pokemon.
         * @param isFavorite Whether to mark as favorite (true) or remove from favorites (false).
         * @return True if successful, false otherwise.
         */
        @AppFunction(isDescribedByKDoc = true)
        suspend fun toggleFavorite(
            appFunctionContext: AppFunctionContext,
            pokemonId: Int,
            isFavorite: Boolean,
        ): Boolean =
            withContext(Dispatchers.IO) {
                if (pokemonId <= 0) {
                    return@withContext false
                }
                val result = pokemonRepository.toggleFavoriteStatus(pokemonId, isFavorite)
                result.isSuccess
            }
    }
