package com.example.pokedex.di

import com.example.pokedex.data.repository.AuthRepositoryImpl
import com.example.pokedex.data.repository.DemoAuthRepository
import com.example.pokedex.data.repository.PokemonRepositoryImpl
import com.example.pokedex.domain.repository.AuthRepository
import com.example.pokedex.domain.repository.PokemonRepository
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

/**
 * Connects data-layer implementations to domain repository contracts.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    fun providePokemonRepository(impl: PokemonRepositoryImpl): PokemonRepository = impl

    @Provides
    @Singleton
    fun provideAuthRepository(
        @Named("firebase_configured") firebaseConfigured: Boolean,
        firebase: Lazy<AuthRepositoryImpl>,
        demo: DemoAuthRepository,
    ): AuthRepository = if (firebaseConfigured) firebase.get() else demo
}
