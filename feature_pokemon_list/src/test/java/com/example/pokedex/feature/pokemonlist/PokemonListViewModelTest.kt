package com.example.pokedex.feature.pokemonlist

import androidx.paging.PagingData
import app.cash.turbine.test
import com.example.pokedex.domain.model.Pokemon
import com.example.pokedex.domain.repository.PokemonRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonListViewModelTest {

    @JvmField
    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension()

    private val repository: PokemonRepository = mockk(relaxed = true)
    private lateinit var viewModel: PokemonListViewModel

    @BeforeEach
    fun setUp() {
        coEvery { repository.getPokemonTypes() } returns Result.success(listOf("Grass", "Poison"))
        coEvery { repository.observeFavoritePokemonIds() } returns flowOf(setOf(1, 4))
        coEvery { repository.getPokemonPaged(any()) } returns flowOf(PagingData.empty())

        viewModel = PokemonListViewModel(repository)
    }

    @Test
    fun `initial state loads types successfully`() = runTest(mainDispatcherExtension.testDispatcher) {
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            cancelAndIgnoreRemainingEvents()
            assertEquals(listOf("Grass", "Poison"), state.availableTypes)
        }
    }

    @Test
    fun `OnSearchQueryChanged updates state query`() = runTest(mainDispatcherExtension.testDispatcher) {
        viewModel.setEvent(PokemonListEvent.OnSearchQueryChanged("pikachu"))
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            cancelAndIgnoreRemainingEvents()
            assertEquals("pikachu", state.searchQuery)
        }
    }

    @Test
    fun `OnTypeFilterSelected updates state selectedType`() = runTest(mainDispatcherExtension.testDispatcher) {
        viewModel.setEvent(PokemonListEvent.OnTypeFilterSelected("Fire"))
        advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            cancelAndIgnoreRemainingEvents()
            assertEquals("Fire", state.selectedType)
        }
    }

    @Test
    fun `OnPokemonClicked emits NavigateToDetail effect`() = runTest(mainDispatcherExtension.testDispatcher) {
        viewModel.uiEffect.test {
            viewModel.setEvent(PokemonListEvent.OnPokemonClicked(25))

            val effect = awaitItem()
            assertTrue(effect is PokemonListEffect.NavigateToDetail)
            assertEquals(25, (effect as PokemonListEffect.NavigateToDetail).pokemonId)
            
            cancelAndIgnoreRemainingEvents()
        }
    }
}
