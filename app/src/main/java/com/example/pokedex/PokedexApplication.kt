package com.example.pokedex

import android.app.Application
import androidx.appfunctions.service.AppFunctionConfiguration
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import com.example.pokedex.data.sync.SyncWorker
import com.example.pokedex.appfunctions.PokedexAppFunctions

@HiltAndroidApp
/**
 * This class is responsible for PokedexApplication logic.
 * Part of the Clean Architecture structure.
 */
class PokedexApplication : Application(), Configuration.Provider, AppFunctionConfiguration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var pokedexAppFunctions: PokedexAppFunctions

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override val appFunctionConfiguration: AppFunctionConfiguration =
        AppFunctionConfiguration.Builder()
            .addEnclosingClassFactory(PokedexAppFunctions::class.java) { pokedexAppFunctions }
            .build()

    override fun onCreate() {
        super.onCreate()
        
        SyncWorker.enqueuePeriodicSync(this)
    }
}
