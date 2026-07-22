package com.example.pokedex.domain.repository

import com.example.pokedex.domain.model.Pokemon


/**
 * This interface is responsible for PokemonRepository logic.
 * Part of the Clean Architecture structure.
 */
interface PokemonRepository {
    suspend fun getPokemonList(limit: Int, offset: Int): Result<List<Pokemon>>
    suspend fun getPokemonDetail(id: Int): Result<Pokemon>
}
