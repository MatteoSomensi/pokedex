package com.example.pokedex.feature.pokemonlist

import app.cash.turbine.test
import com.example.pokedex.core.R
import com.example.pokedex.core.util.UiText
import com.example.pokedex.domain.model.Pokemon
import com.example.pokedex.domain.repository.PokemonRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonListViewModelTest {

    @JvmField
    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension()

    private val repository: PokemonRepository = mockk()
    private lateinit var viewModel: PokemonListViewModel

    @BeforeEach
    fun setUp() {
        // Mock di base per il blocco init del ViewModel
        coEvery { repository.getPokemonTypes() } returns Result.success(listOf("Grass", "Poison"))
        coEvery { repository.observeFavoritePokemonIds() } returns flowOf(emptySet())
    }

    @Test
    fun `when LoadPokemon succeeds then state emits Loading and then populates list`() = runTest(mainDispatcherExtension.testDispatcher) {
        // GIVEN
        val fakeList = listOf(
            Pokemon(1, "Bulbasaur", "url", "cryUrl", listOf("Grass"))
        )
        // Quando init chiama loadPokemon tramite l'evento
        coEvery { repository.getPokemonList(any(), any(), any()) } returns Result.success(fakeList)

        // Creazione del ViewModel (chiama init block, che lancia loadPokemon e observeFavorites)
        viewModel = PokemonListViewModel(repository)

        // Avanziamo il tempo virtuale affinché tutte le coroutine lanciate in init finiscano
        advanceUntilIdle()

        // WHEN & THEN
        viewModel.uiState.test {
            // Al completamento, ci aspettiamo lo stato finale popolato
            val finalState = awaitItem()
            cancelAndIgnoreRemainingEvents()
            assertFalse(finalState.isLoading)
            assertEquals(fakeList, finalState.pokemonList)
            assertEquals(fakeList, finalState.filteredPokemonList)
            assertEquals(listOf("Grass", "Poison"), finalState.availableTypes)
            assertEquals(null, finalState.errorMessage)
        }
    }

    @Test
    fun `when LoadPokemon fails then errorMessage is populated`() = runTest(mainDispatcherExtension.testDispatcher) {
        // GIVEN
        val exception = RuntimeException("Network Error")
        coEvery { repository.getPokemonList(any(), any(), any()) } returns Result.failure(exception)

        // Creazione ViewModel
        viewModel = PokemonListViewModel(repository)

        advanceUntilIdle()

        // WHEN & THEN
        viewModel.uiState.test {
            val finalState = awaitItem()
            cancelAndIgnoreRemainingEvents()
            assertFalse(finalState.isLoading)
            // L'errore mappato nel ViewModel per fallback è una risorsa stringa
            assertTrue(finalState.errorMessage is UiText.StringResource)
            assertEquals(R.string.error_default, (finalState.errorMessage as UiText.StringResource).id)
        }
    }

    @Test
    fun `when OnPokemonClicked is called then NavigateToDetail effect is emitted`() = runTest(mainDispatcherExtension.testDispatcher) {
        // GIVEN
        coEvery { repository.getPokemonList(any(), any(), any()) } returns Result.success(emptyList())
        viewModel = PokemonListViewModel(repository)
        advanceUntilIdle() // facciamo finire l'inizializzazione

        // WHEN & THEN testiamo lo SharedFlow degli effetti (uiEffect)
        viewModel.uiEffect.test {
            // Azione
            viewModel.setEvent(PokemonListEvent.OnPokemonClicked(25))

            // Verifica dell'evento
            val effect = awaitItem()
            assertEquals(PokemonListEffect.NavigateToDetail(25), effect)

            expectNoEvents()
        }
    }
    @Test
    fun `when LoadNextPage succeeds then new items are appended`() = runTest(mainDispatcherExtension.testDispatcher) {
        // GIVEN
        val firstPage = List(20) { Pokemon(it, "Pokemon $it", "url", "cryUrl", listOf("Grass")) }
        val secondPage = listOf(Pokemon(20, "Ivysaur", "url", "cryUrl", listOf("Grass")))
        
        coEvery { repository.getPokemonList(limit = any(), offset = eq(0), forceRefresh = any()) } returns Result.success(firstPage)
        coEvery { repository.getPokemonList(limit = any(), offset = eq(20), forceRefresh = any()) } returns Result.success(secondPage)

        viewModel = PokemonListViewModel(repository)
        advanceUntilIdle() // Initial load completes

        // WHEN
        viewModel.setEvent(PokemonListEvent.LoadNextPage)
        advanceUntilIdle() // Load next page completes

        // THEN
        viewModel.uiState.test {
            val finalState = awaitItem()
            cancelAndIgnoreRemainingEvents()
            assertFalse(finalState.isFetchingNextPage)
            assertEquals(21, finalState.pokemonList.size)
            assertEquals(21, finalState.offset)
            assertEquals(firstPage + secondPage, finalState.pokemonList)
        }
    }

    @Test
    fun `when OnSearchQueryChanged then search is performed after debounce`() = runTest(mainDispatcherExtension.testDispatcher) {
        // GIVEN
        coEvery { repository.getPokemonList(limit = any(), offset = any(), forceRefresh = any()) } returns Result.success(emptyList())
        val searchResult = listOf(Pokemon(25, "Pikachu", "url", "cryUrl", listOf("Electric")))
        coEvery { repository.searchPokemon(query = eq("pika"), limit = any(), offset = eq(0)) } returns Result.success(searchResult)

        viewModel = PokemonListViewModel(repository)
        advanceUntilIdle() // Initial load

        // WHEN
        viewModel.setEvent(PokemonListEvent.OnSearchQueryChanged("pika"))
        advanceUntilIdle() // Wait for debounce and search coroutine

        // THEN
        viewModel.uiState.test {
            val finalState = awaitItem()
            cancelAndIgnoreRemainingEvents()
            assertEquals("pika", finalState.searchQuery)
            assertEquals(searchResult, finalState.pokemonList)
            assertEquals(searchResult, finalState.filteredPokemonList)
        }
    }

    @Test
    fun `when OnTypeFilterSelected then list is filtered locally`() = runTest(mainDispatcherExtension.testDispatcher) {
        // GIVEN
        val bulbasaur = Pokemon(1, "Bulbasaur", "url", "cryUrl", listOf("Grass", "Poison"))
        val charmander = Pokemon(4, "Charmander", "url", "cryUrl", listOf("Fire"))
        val initialList = listOf(bulbasaur, charmander)
        coEvery { repository.getPokemonList(limit = any(), offset = any(), forceRefresh = any()) } returns Result.success(initialList)
        
        viewModel = PokemonListViewModel(repository)
        advanceUntilIdle()

        // WHEN
        viewModel.setEvent(PokemonListEvent.OnTypeFilterSelected("Fire"))
        advanceUntilIdle()

        // THEN
        viewModel.uiState.test {
            val finalState = awaitItem()
            cancelAndIgnoreRemainingEvents()
            assertEquals("Fire", finalState.selectedType)
            assertEquals(listOf(charmander), finalState.filteredPokemonList)
            // L'intera lista non cambia
            assertEquals(initialList, finalState.pokemonList) 
        }
    }
}
