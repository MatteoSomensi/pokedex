package com.example.pokedex.core.util

/** Public endpoints shared by the template's network and image implementations. */
object Constants {
    /** PokeAPI REST base URL, including the trailing slash required by Retrofit. */
    const val POKE_API_BASE_URL = "https://pokeapi.co/api/v2/"

    /** Official sprite base URL; append a Pokémon ID and the `.png` suffix. */
    const val POKE_IMAGE_BASE_URL =
        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/"
}
