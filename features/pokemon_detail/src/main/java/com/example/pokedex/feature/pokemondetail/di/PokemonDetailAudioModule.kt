package com.example.pokedex.feature.pokemondetail.di

import com.example.pokedex.feature.pokemondetail.audio.AndroidPokemonCryPlayer
import com.example.pokedex.feature.pokemondetail.audio.PokemonCryPlayer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/** Provides a ViewModel-scoped audio controller for the Pokémon detail feature. */
@Module
@InstallIn(ViewModelComponent::class)
abstract class PokemonDetailAudioModule {
    @Binds
    @ViewModelScoped
    abstract fun bindPokemonCryPlayer(implementation: AndroidPokemonCryPlayer): PokemonCryPlayer
}
