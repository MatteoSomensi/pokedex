package com.example.pokedex.appfunctions

import androidx.appfunctions.service.AppFunction
import androidx.appfunctions.AppFunctionContext
import com.example.pokedex.domain.repository.PokemonRepository
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PokedexAppFunctions @Inject constructor(
    private val pokemonRepository: PokemonRepository
) {
    /**
     * Search for a Pokemon by name.
     *
     * @param appFunctionContext The execution context.
     * @param query The name or partial name of the Pokemon to search for.
     * @return A message describing the search result.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun searchPokemon(
        appFunctionContext: AppFunctionContext,
        query: String
    ): String = withContext(Dispatchers.IO) {
        // Simple demonstration
        // A real implementation would return a serialized @AppFunctionSerializable class
        val result = pokemonRepository.searchPokemon(query.lowercase(), limit = 5, offset = 0)
        if (result.isSuccess) {
            val pokemons = result.getOrNull()
            if (!pokemons.isNullOrEmpty()) {
                val p = pokemons.first()
                "Found ${p.name}."
            } else {
                "Could not find Pokemon with name $query."
            }
        } else {
            "Error searching for Pokemon $query."
        }
    }

    /**
     * Favorite or unfavorite a Pokemon.
     *
     * @param appFunctionContext The execution context.
     * @param pokemonId The numeric Pokemon identifier.
     * @param isFavorite Whether the Pokemon should be marked as favorite.
     * @return A message describing the operation result.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun toggleFavorite(
        appFunctionContext: AppFunctionContext,
        pokemonId: Int,
        isFavorite: Boolean
    ): String = withContext(Dispatchers.IO) {
        if (pokemonId <= 0) {
            return@withContext "Pokemon id must be greater than zero."
        }

        pokemonRepository.toggleFavoriteStatus(pokemonId, isFavorite).fold(
            onSuccess = {
                if (isFavorite) {
                    "Pokemon $pokemonId added to favorites."
                } else {
                    "Pokemon $pokemonId removed from favorites."
                }
            },
            onFailure = {
                "Could not update favorite status for Pokemon $pokemonId."
            }
        )
    }
}
