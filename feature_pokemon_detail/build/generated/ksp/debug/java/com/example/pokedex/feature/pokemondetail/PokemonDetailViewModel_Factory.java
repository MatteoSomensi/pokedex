package com.example.pokedex.feature.pokemondetail;

import androidx.lifecycle.SavedStateHandle;
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
public final class PokemonDetailViewModel_Factory implements Factory<PokemonDetailViewModel> {
  private final Provider<PokemonRepository> repositoryProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private PokemonDetailViewModel_Factory(Provider<PokemonRepository> repositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.repositoryProvider = repositoryProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public PokemonDetailViewModel get() {
    return newInstance(repositoryProvider.get(), savedStateHandleProvider.get());
  }

  public static PokemonDetailViewModel_Factory create(
      Provider<PokemonRepository> repositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new PokemonDetailViewModel_Factory(repositoryProvider, savedStateHandleProvider);
  }

  public static PokemonDetailViewModel newInstance(PokemonRepository repository,
      SavedStateHandle savedStateHandle) {
    return new PokemonDetailViewModel(repository, savedStateHandle);
  }
}
