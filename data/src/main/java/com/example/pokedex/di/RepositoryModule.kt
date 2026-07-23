package com.example.pokedex.di

import com.example.pokedex.data.repository.PokemonRepositoryImpl
import com.example.pokedex.domain.repository.PokemonRepository
import com.example.pokedex.data.repository.AuthRepositoryImpl
import com.example.pokedex.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)

/**
 * This class is responsible for RepositoryModule logic.
 * Part of the Clean Architecture structure.
 */
abstract class RepositoryModule {

    @Binds
    abstract fun bindPokemonRepository(
        impl: PokemonRepositoryImpl
    ): PokemonRepository

    @Binds
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository
}
