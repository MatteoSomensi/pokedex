package com.example.pokedex.data.sync

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import androidx.work.ListenableWorker.Result
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import com.example.pokedex.data.coroutines.TestDispatcherProvider
import com.example.pokedex.domain.model.Pokemon
import com.example.pokedex.domain.repository.PokemonRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SyncWorkerTest {
    private lateinit var context: Context
    private lateinit var pokemonRepository: PokemonRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        pokemonRepository = mockk()
    }

    @Test
    fun `doWork returns success when repository fetch succeeds`() =
        runTest {
            // Arrange
            val mockPokemon =
                Pokemon(
                    id = 1,
                    name = "Bulbasaur",
                    imageUrl = "",
                    cryUrl = "",
                    types = emptyList(),
                    height = 7,
                    weight = 69,
                    stats = emptyMap(),
                )
            coEvery {
                pokemonRepository.getPokemonList(any(), any(), any())
            } returns kotlin.Result.success(listOf(mockPokemon))

            val worker =
                TestListenableWorkerBuilder<SyncWorker>(context)
                    .setWorkerFactory(
                        object : WorkerFactory() {
                            override fun createWorker(
                                appContext: Context,
                                workerClassName: String,
                                workerParameters: WorkerParameters,
                            ): ListenableWorker =
                                SyncWorker(
                                    appContext,
                                    workerParameters,
                                    pokemonRepository,
                                    TestDispatcherProvider(),
                                )
                        },
                    ).build()

            // Act
            val result = worker.doWork()

            // Assert
            assertEquals(Result.success(), result)
        }

    @Test
    fun `doWork returns retry when repository fetch fails`() =
        runTest {
            // Arrange
            coEvery {
                pokemonRepository.getPokemonList(any(), any(), any())
            } returns kotlin.Result.failure(Exception("Network error"))

            val worker =
                TestListenableWorkerBuilder<SyncWorker>(context)
                    .setWorkerFactory(
                        object : WorkerFactory() {
                            override fun createWorker(
                                appContext: Context,
                                workerClassName: String,
                                workerParameters: WorkerParameters,
                            ): ListenableWorker =
                                SyncWorker(
                                    appContext,
                                    workerParameters,
                                    pokemonRepository,
                                    TestDispatcherProvider(),
                                )
                        },
                    ).build()

            // Act
            val result = worker.doWork()

            // Assert
            assertEquals(Result.retry(), result)
        }
}
