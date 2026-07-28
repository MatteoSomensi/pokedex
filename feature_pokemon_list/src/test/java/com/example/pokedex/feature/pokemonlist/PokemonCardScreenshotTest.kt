package com.example.pokedex.feature.pokemonlist

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.pokedex.domain.model.Pokemon
import com.example.pokedex.theme.PokedexTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel5)
class PokemonCardScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun defaultPokemonCard() {
        val dummyPokemon = Pokemon(
            id = 1,
            name = "bulbasaur",
            url = "https://pokeapi.co/api/v2/pokemon/1/",
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png",
            types = listOf("grass", "poison")
        )

        composeTestRule.setContent {
            PokedexTheme {
                PokemonCard(pokemon = dummyPokemon, onClick = {})
            }
        }

        composeTestRule.onRoot().captureRoboImage()
    }
}
