package com.example.pokedex.data.remote

import com.example.pokedex.data.remote.model.PokemonDetailResponse
import com.example.pokedex.data.remote.model.PokemonListResponse
import com.example.pokedex.data.remote.model.TypeListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit contract for the PokeAPI endpoints consumed by the app.
 *
 * Transport models are intentionally returned unchanged; mapping, caching, retry policy, and error
 * handling belong to the repository layer.
 */
interface PokeApiService {
    /** Returns a page of lightweight Pokémon references. */
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int = 151,
        @Query("offset") offset: Int = 0,
    ): PokemonListResponse

    /** Returns one Pokémon resolved by its case-insensitive name or numeric identifier. */
    @GET("pokemon/{nameOrId}")
    suspend fun getPokemonDetail(
        @Path("nameOrId") nameOrId: String,
    ): PokemonDetailResponse

    /** Returns all type resources known to PokeAPI. */
    @GET("type")
    suspend fun getPokemonTypes(): TypeListResponse
}
