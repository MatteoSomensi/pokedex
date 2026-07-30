package com.example.pokedex.feature.pokemonlist

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.pokedex.domain.model.Pokemon
import com.example.pokedex.theme.PokedexTheme
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PokemonListScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun whenStateHasPokemon_thenTheyAreDisplayed() {
        val mockPokemon =
            listOf(
                Pokemon(id = 1, name = "Bulbasaur", imageUrl = "", cryUrl = "", types = listOf("Grass")),
                Pokemon(id = 2, name = "Ivysaur", imageUrl = "", cryUrl = "", types = listOf("Grass")),
            )
        val mockState = PokemonListState()
        val pokemonFlow = flowOf(PagingData.from(mockPokemon))

        composeTestRule.setContent {
            PokedexTheme {
                val pagedPokemon = pokemonFlow.collectAsLazyPagingItems()
                PokemonListScreenContent(
                    state = mockState,
                    pagedPokemon = pagedPokemon,
                    onEvent = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Bulbasaur").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ivysaur").assertIsDisplayed()
    }
}
