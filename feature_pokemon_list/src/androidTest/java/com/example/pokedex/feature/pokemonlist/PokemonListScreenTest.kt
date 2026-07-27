package com.example.pokedex.feature.pokemonlist

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.pokedex.domain.model.Pokemon
import com.example.pokedex.theme.PokedexTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PokemonListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun whenStateHasPokemon_thenTheyAreDisplayed() {
        // Given
        val mockPokemon = listOf(
            Pokemon(id = 1, name = "Bulbasaur", imageUrl = "", cryUrl = "", types = listOf("Grass")),
            Pokemon(id = 2, name = "Ivysaur", imageUrl = "", cryUrl = "", types = listOf("Grass"))
        )
        val mockState = PokemonListState(
            isLoading = false,
            pokemonList = mockPokemon,
            filteredPokemonList = mockPokemon
        )

        // When
        composeTestRule.setContent {
            PokedexTheme {
                PokemonListScreenContent(
                    state = mockState,
                    onEvent = {},
                    onNavigateToProfile = {}
                )
            }
        }

        // Then
        // Verifica che Bulbasaur e Ivysaur siano visibili nello schermo
        composeTestRule.onNodeWithText("Bulbasaur").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ivysaur").assertIsDisplayed()
    }
}
