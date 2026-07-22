package com.example.pokedex.feature.pokemonlist;

import com.example.pokedex.domain.repository.PokemonRepository;
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
public final class PokemonListViewModel_Factory implements Factory<PokemonListViewModel> {
  private final Provider<PokemonRepository> repositoryProvider;

  private PokemonListViewModel_Factory(Provider<PokemonRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public PokemonListViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static PokemonListViewModel_Factory create(
      Provider<PokemonRepository> repositoryProvider) {
    return new PokemonListViewModel_Factory(repositoryProvider);
  }

  public static PokemonListViewModel newInstance(PokemonRepository repository) {
    return new PokemonListViewModel(repository);
  }
}
