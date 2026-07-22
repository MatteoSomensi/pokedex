package com.example.pokedex.data.repository;

import com.example.pokedex.data.remote.PokeApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class PokemonRepositoryImpl_Factory implements Factory<PokemonRepositoryImpl> {
  private final Provider<PokeApiService> apiProvider;

  private PokemonRepositoryImpl_Factory(Provider<PokeApiService> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public PokemonRepositoryImpl get() {
    return newInstance(apiProvider.get());
  }

  public static PokemonRepositoryImpl_Factory create(Provider<PokeApiService> apiProvider) {
    return new PokemonRepositoryImpl_Factory(apiProvider);
  }

  public static PokemonRepositoryImpl newInstance(PokeApiService api) {
    return new PokemonRepositoryImpl(api);
  }
}
