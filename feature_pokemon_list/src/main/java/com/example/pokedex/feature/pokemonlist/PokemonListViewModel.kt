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
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
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

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val pagedPokemonFlow: Flow<PagingData<Pokemon>> = combine(
        uiState
            .map { it.searchQuery.trim() }
            .distinctUntilChanged()
            .debounce(SEARCH_DEBOUNCE_MILLIS),
        uiState
            .map { it.selectedType }
            .distinctUntilChanged()
    ) { query, selectedType ->
        query to selectedType
    }
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
        }
        .cachedIn(viewModelScope)

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

    private companion object {
        const val SEARCH_DEBOUNCE_MILLIS = 300L
    }
}
