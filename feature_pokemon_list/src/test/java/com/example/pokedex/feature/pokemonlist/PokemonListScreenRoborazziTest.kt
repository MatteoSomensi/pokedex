package com.example.pokedex.feature.pokemonlist

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.pokedex.domain.model.Pokemon
import com.example.pokedex.theme.PokedexTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = RobolectricDeviceQualifiers.Pixel5)
class PokemonListScreenRoborazziTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun capturePokemonListScreen() {
        System.setProperty("roborazzi.test.record", "true")
        val mockPokemon = listOf(
            Pokemon(id = 1, name = "Bulbasaur", imageUrl = "", cryUrl = "", types = listOf("Grass")),
            Pokemon(id = 2, name = "Ivysaur", imageUrl = "", cryUrl = "", types = listOf("Grass"))
        )
        val mockState = PokemonListState(
            isLoading = false,
            pokemonList = mockPokemon,
            filteredPokemonList = mockPokemon
        )

        composeTestRule.setContent {
            PokedexTheme {
                PokemonListScreenContent(
                    state = mockState,
                    onEvent = {},
                    onNavigateToProfile = {},
                    onNavigateToFavorites = {}
                )
            }
        }

        // Catturiamo lo screenshot e lo salviamo!
        composeTestRule.onRoot().captureRoboImage(filePath = "build/outputs/roborazzi/PokemonListScreen.png")
    }
}
