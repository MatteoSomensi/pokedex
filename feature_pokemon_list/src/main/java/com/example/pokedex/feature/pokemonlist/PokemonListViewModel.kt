package com.example.pokedex.feature.pokemonlist

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.example.pokedex.core.mvi.BaseViewModel
import com.example.pokedex.domain.model.Pokemon
import com.example.pokedex.domain.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository
) : BaseViewModel<PokemonListState, PokemonListEvent, PokemonListEffect>() {

    override fun createInitialState(): PokemonListState = PokemonListState()

    init {
        loadTypes()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagedPokemonFlow: Flow<PagingData<Pokemon>> = uiState
        .map { it.searchQuery to it.selectedType }
        .distinctUntilChanged()
        .flatMapLatest { (query, selectedType) ->
            repository.getPokemonPaged(query).map { pagingData ->
                if (selectedType != null) {
                    pagingData.filter { pokemon ->
                        pokemon.types.any { it.equals(selectedType, ignoreCase = true) }
                    }
                } else {
                    pagingData
                }
            }
        }.cachedIn(viewModelScope)
        .combine(repository.observeFavoritePokemonIds()) { pagingData, favoriteIds ->
            pagingData.map { pokemon ->
                pokemon.copy(isFavorite = favoriteIds.contains(pokemon.id))
            }
        }

    private fun loadTypes() {
        viewModelScope.launch {
            repository.getPokemonTypes().onSuccess { types ->
                setState { copy(availableTypes = types) }
            }
        }
    }

    override fun handleEvent(event: PokemonListEvent) {
        when (event) {
            is PokemonListEvent.OnPokemonClicked -> {
                setEffect { PokemonListEffect.NavigateToDetail(pokemonId = event.pokemonId) }
            }
            is PokemonListEvent.OnSearchQueryChanged -> {
                setState { copy(searchQuery = event.query) }
            }
            is PokemonListEvent.OnTypeFilterSelected -> {
                setState { copy(selectedType = event.type) }
            }
        }
    }
}
