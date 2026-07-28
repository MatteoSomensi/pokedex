package com.example.pokedex

import android.app.Application
import androidx.appfunctions.service.AppFunctionConfiguration
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.pokedex.appfunctions.PokedexAppFunctions
import com.example.pokedex.data.sync.SyncWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application entry point for Hilt, WorkManager, and AppFunctions.
 *
 * Supplies Hilt's worker factory to WorkManager, registers the injected AppFunctions enclosing
 * class, and enqueues the unique periodic synchronization during process startup.
 */
@HiltAndroidApp
class PokedexApplication :
    Application(),
    Configuration.Provider,
    AppFunctionConfiguration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var pokedexAppFunctions: PokedexAppFunctions

    override val workManagerConfiguration: Configuration
        get() =
            Configuration
                .Builder()
                .setWorkerFactory(workerFactory)
                .build()

    override val appFunctionConfiguration: AppFunctionConfiguration =
        AppFunctionConfiguration
            .Builder()
            .addEnclosingClassFactory(PokedexAppFunctions::class.java) { pokedexAppFunctions }
            .build()

    override fun onCreate() {
        super.onCreate()

        SyncWorker.enqueuePeriodicSync(this)
    }
}
