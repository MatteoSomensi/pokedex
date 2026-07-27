package com.example.pokedex

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import com.example.pokedex.data.sync.SyncWorker

@HiltAndroidApp
/**
 * This class is responsible for PokedexApplication logic.
 * Part of the Clean Architecture structure.
 */
class PokedexApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        
        SyncWorker.enqueuePeriodicSync(this)
    }
}
