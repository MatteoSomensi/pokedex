package com.example.pokedex.core.di

import com.example.pokedex.core.coroutines.DefaultDispatcherProvider
import com.example.pokedex.core.coroutines.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Connects [DispatcherProvider] to its production implementation in the Hilt graph. */
@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {
    @Provides
    @Singleton
    fun provideDispatcherProvider(): DispatcherProvider = DefaultDispatcherProvider()
}
