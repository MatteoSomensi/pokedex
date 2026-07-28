package com.example.pokedex.data.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import coil.imageLoader
import coil.request.ImageRequest
import com.example.pokedex.core.coroutines.DispatcherProvider
import com.example.pokedex.domain.repository.PokemonRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * A WorkManager CoroutineWorker for syncing Pokedex data and caching images.
 * Annotated with [HiltWorker] to allow dependency injection via Hilt.
 *
 * This worker performs the following operations:
 * 1. Fetches a predetermined list of Pokemon from the repository.
 * 2. Caches the network responses locally in the database.
 * 3. Iterates through the fetched list to download and cache images using Coil.
 *
 * @property appContext The application context.
 * @property workerParams Parameters to setup the internal state of this worker.
 * @property pokemonRepository The repository used to fetch Pokemon data.
 */
@HiltWorker
class SyncWorker
    @AssistedInject
    constructor(
        @Assisted appContext: Context,
        @Assisted workerParams: WorkerParameters,
        private val pokemonRepository: PokemonRepository,
        private val dispatchers: DispatcherProvider,
    ) : CoroutineWorker(appContext, workerParams) {
        /**
         * Executes the background sync logic.
         *
         * @return [Result.success] if all data and images were synced successfully,
         *         [Result.retry] if a network error occurred or the data fetch failed.
         */
        override suspend fun doWork(): Result =
            withContext(dispatchers.io) {
                try {
                    Log.d(TAG, "Starting sync work...")

                    val result = pokemonRepository.getPokemonList(limit = 151, offset = 0, forceRefresh = true)

                    if (result.isSuccess) {
                        val pokemonList = result.getOrNull() ?: emptyList()
                        val imageUrls =
                            pokemonList.mapNotNull { pokemon ->
                                pokemon.imageUrl.takeIf(String::isNotEmpty)
                            }

                        if (imageUrls.isNotEmpty()) {
                            val imageLoader = applicationContext.imageLoader
                            imageUrls.chunked(IMAGE_PREFETCH_CONCURRENCY).forEach { chunk ->
                                coroutineScope {
                                    chunk
                                        .map { imageUrl ->
                                            async {
                                                val request =
                                                    ImageRequest
                                                        .Builder(applicationContext)
                                                        .data(imageUrl)
                                                        .build()
                                                imageLoader.execute(request)
                                            }
                                        }.awaitAll()
                                }
                            }
                        }

                        Log.d(TAG, "Sync work finished successfully. Synced ${pokemonList.size} Pokémon.")
                        Result.success()
                    } else {
                        Log.e(TAG, "Error fetching data during sync", result.exceptionOrNull())
                        Result.retry()
                    }
                } catch (exception: CancellationException) {
                    throw exception
                } catch (e: Exception) {
                    Log.e(TAG, "Exception during sync work", e)
                    Result.retry()
                }
            }

        companion object {
            private const val TAG = "SyncWorker"
            const val PERIODIC_WORK_NAME = "PokemonPeriodicDataSyncWork"
            const val MANUAL_WORK_NAME = "PokemonManualDataSyncWork"
            const val SYNC_TAG = "PokemonDataSync"
            private const val IMAGE_PREFETCH_CONCURRENCY = 10

            /**
             * Schedules a periodic sync to run in the background.
             * The work requires an active network connection and runs approximately once a day.
             *
             * @param context The context used to retrieve the [WorkManager] instance.
             */
            fun enqueuePeriodicSync(context: Context) {
                val constraints =
                    Constraints
                        .Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()

                val syncRequest =
                    PeriodicWorkRequestBuilder<SyncWorker>(1, TimeUnit.DAYS)
                        .setConstraints(constraints)
                        .addTag(SYNC_TAG)
                        .build()

                val workManager = WorkManager.getInstance(context)
                workManager.enqueueUniquePeriodicWork(
                    PERIODIC_WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    syncRequest,
                )
            }
        }
    }
