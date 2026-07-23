package com.example.pokedex

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object PokemonList : NavKey
@Serializable data class PokemonDetail(val id: Int) : NavKey
@Serializable data object Auth : NavKey
@Serializable data object Profile : NavKey
