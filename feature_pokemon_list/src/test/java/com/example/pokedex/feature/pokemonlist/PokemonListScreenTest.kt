package com.example.pokedex.feature.pokemonlist

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.pokedex.domain.model.Pokemon
import com.example.pokedex.theme.PokedexTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = RobolectricDeviceQualifiers.Pixel5)
class PokemonListScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun pokemonListScreen_defaultState_looksCorrect() {
        // Arrange
        val mockState =
            PokemonListState(
                availableTypes = listOf("Grass", "Fire", "Water"),
            )

        val mockPokemon =
            listOf(
                Pokemon(
                    id = 1,
                    name = "Bulbasaur",
                    imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png",
                    cryUrl = "",
                    types = listOf("Grass", "Poison"),
                ),
                Pokemon(
                    id = 4,
                    name = "Charmander",
                    imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/4.png",
                    cryUrl = "",
                    types = listOf("Fire"),
                ),
                Pokemon(
                    id = 7,
                    name = "Squirtle",
                    imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/7.png",
                    cryUrl = "",
                    types = listOf("Water"),
                ),
            )

        val flow = flowOf(PagingData.from(mockPokemon))

        // Act
        composeTestRule.setContent {
            PokedexTheme {
                val lazyPagingItems = flow.collectAsLazyPagingItems()
                PokemonListScreenContent(
                    state = mockState,
                    pagedPokemon = lazyPagingItems,
                    onEvent = {},
                    onNavigateToProfile = {},
                    onNavigateToFavorites = {},
                )
            }
        }

        // Assert
        composeTestRule.onRoot().captureRoboImage()
    }
}
