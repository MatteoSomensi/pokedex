package com.example.pokedex.feature.pokemonlist

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import com.example.pokedex.core.designsystem.components.PokemonCard
import com.example.pokedex.core.designsystem.components.PokemonCardModel
import com.example.pokedex.core.ui.AccessibilityPreviews
import com.example.pokedex.core.ui.ScreenSizePreviews
import com.example.pokedex.domain.model.Pokemon
import com.example.pokedex.theme.PokedexTheme

@PreviewTest
@ScreenSizePreviews
@AccessibilityPreviews
@Composable
fun PokemonCardPreview() {
    val dummyPokemon =
        Pokemon(
            id = 1,
            name = "bulbasaur",
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png",
            cryUrl = "https://raw.githubusercontent.com/PokeAPI/cries/main/cries/pokemon/latest/1.ogg",
            types = listOf("grass", "poison"),
        )
    PokedexTheme {
        PokemonCard(
            model =
                PokemonCardModel(
                    name = dummyPokemon.name,
                    imageUrl = dummyPokemon.imageUrl,
                    imageContentDescription = dummyPokemon.name,
                    types = dummyPokemon.types,
                    isFavorite = dummyPokemon.isFavorite,
                ),
            onClick = {},
        )
    }
}
