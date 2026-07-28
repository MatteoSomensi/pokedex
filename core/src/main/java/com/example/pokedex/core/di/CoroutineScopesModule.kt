package com.example.pokedex.core.di

import android.util.Log
import com.example.pokedex.core.coroutines.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoroutineScopesModule {

    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(
        dispatcherProvider: DispatcherProvider
    ): CoroutineScope {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            Log.e("ApplicationScope", "Eccezione non gestita nella coroutine", exception)
        }

        return CoroutineScope(SupervisorJob() + dispatcherProvider.default + exceptionHandler)
    }
}
