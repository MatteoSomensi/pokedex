package com.example.pokedex.feature.pokemonlist

import app.cash.turbine.test
import com.example.pokedex.domain.model.Pokemon
import com.example.pokedex.domain.repository.PokemonRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)

/**
 * Unit tests for [PokemonListViewModel].
 * Verifies state updates, side effects, and interaction with the [PokemonRepository].
 */
class PokemonListViewModelTest {

    private lateinit var viewModel: PokemonListViewModel
    private val repository = mockk<PokemonRepository>()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `quando LoadPokemon ha successo lo stato viene aggiornato con la lista`() =
        runTest(testDispatcher) {
            val fakeList = listOf(
                Pokemon(1, "Bulbasaur", "url", "cryUrl", listOf("Grass"))
            )
            coEvery { repository.getPokemonList(any(), any(), any()) } returns Result.success(fakeList)
            coEvery { repository.getPokemonTypes() } returns Result.success(emptyList())
            io.mockk.every { repository.observeFavoritePokemonIds() } returns kotlinx.coroutines.flow.flowOf(emptySet())

            viewModel = PokemonListViewModel(repository)

            viewModel.uiState.test {
                val state = awaitItem()

                assertEquals(false, state.isLoading)
                assertEquals(fakeList, state.pokemonList)
                assertEquals(null, state.errorMessage)
            }
        }

    @Test
    fun `quando OnPokemonClicked viene chiamato, viene emesso l'effetto NavigateToDetail`() =
        runTest(testDispatcher) {
            coEvery { repository.getPokemonList(any(), any(), any()) } returns Result.success(emptyList())
            coEvery { repository.getPokemonTypes() } returns Result.success(emptyList())
            io.mockk.every { repository.observeFavoritePokemonIds() } returns kotlinx.coroutines.flow.flowOf(emptySet())

            viewModel = PokemonListViewModel(repository)

            viewModel.uiEffect.test {
                viewModel.setEvent(PokemonListEvent.OnPokemonClicked(1))

                val effect = awaitItem()
                assertEquals(PokemonListEffect.NavigateToDetail(1), effect)
            }
        }
}
