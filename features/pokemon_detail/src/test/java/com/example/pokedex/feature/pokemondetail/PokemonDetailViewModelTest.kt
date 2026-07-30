package com.example.pokedex.feature.pokemondetail

import app.cash.turbine.test
import com.example.pokedex.domain.model.Pokemon
import com.example.pokedex.domain.repository.PokemonRepository
import com.example.pokedex.domain.result.AppResult
import com.example.pokedex.feature.pokemondetail.audio.PokemonCryPlayer
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonDetailViewModelTest {
    @JvmField
    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension()

    private val repository: PokemonRepository = mockk(relaxed = true)
    private val cryPlayer: PokemonCryPlayer = mockk(relaxed = true)
    private lateinit var viewModel: PokemonDetailViewModel

    @BeforeEach
    fun setUp() {
        coEvery { repository.getPokemonDetail(POKEMON.id) } returns AppResult.success(POKEMON)
        coEvery { cryPlayer.play(any()) } returns Result.success(Unit)
        viewModel = PokemonDetailViewModel(repository = repository, cryPlayer = cryPlayer)
    }

    @Test
    fun `PlayCry delegates playback to the injected player`() =
        runTest(mainDispatcherExtension.testDispatcher) {
            viewModel.handleEvent(PokemonDetailEvent.LoadPokemon(id = POKEMON.id))
            advanceUntilIdle()

            viewModel.handleEvent(PokemonDetailEvent.PlayCry)
            advanceUntilIdle()

            coVerify(exactly = 1) { cryPlayer.play(POKEMON.cryUrl) }
        }

    @Test
    fun `PlayCry emits an error effect when playback cannot start`() =
        runTest(mainDispatcherExtension.testDispatcher) {
            coEvery { cryPlayer.play(POKEMON.cryUrl) } returns
                Result.failure(IllegalStateException("Playback failed"))
            viewModel.handleEvent(PokemonDetailEvent.LoadPokemon(id = POKEMON.id))
            advanceUntilIdle()

            viewModel.uiEffect.test {
                viewModel.handleEvent(PokemonDetailEvent.PlayCry)

                assert(awaitItem() == PokemonDetailEffect.ShowPlaybackError)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `PlayCry does nothing before a Pokemon is loaded`() =
        runTest(mainDispatcherExtension.testDispatcher) {
            viewModel.handleEvent(PokemonDetailEvent.PlayCry)
            advanceUntilIdle()

            coVerify(exactly = 0) { cryPlayer.play(any()) }
        }

    private companion object {
        val POKEMON =
            Pokemon(
                id = 25,
                name = "pikachu",
                imageUrl = "image",
                cryUrl = "https://example.com/pikachu.ogg",
                types = listOf("Electric"),
            )
    }
}
