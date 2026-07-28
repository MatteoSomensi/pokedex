package com.example.pokedex.appfunctions

import androidx.appfunctions.AppFunctionContext
import androidx.appfunctions.service.AppFunction
import com.example.pokedex.domain.repository.PokemonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Agent-callable Pokedex workflows backed by [PokemonRepository].
 *
 * Each function opts into KDoc-derived descriptions, so its documentation is part of the
 * machine-readable contract exposed to callers.
 */
class PokedexAppFunctions
    @Inject
    constructor(
        private val pokemonRepository: PokemonRepository,
    ) {
        /**
         * Searches for Pokémon by full or partial name.
         *
         * Call this function before [toggleFavorite] to obtain a valid numeric Pokémon ID.
         *
         * @param appFunctionContext execution context supplied by the AppFunctions runtime.
         * @param query full or partial Pokémon name.
         * @return at most ten matching [PokemonResult] values, or an empty list after failure or no match.
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
         * Sets the favorite status of a Pokémon.
         *
         * Call [searchPokemon] first instead of guessing an identifier.
         *
         * @param appFunctionContext execution context supplied by the AppFunctions runtime.
         * @param pokemonId positive numeric Pokémon identifier.
         * @param isFavorite `true` to add the favorite, `false` to remove it.
         * @return `true` after persistence succeeds; `false` for invalid IDs or repository failures.
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
