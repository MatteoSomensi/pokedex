package com.example.pokedex.feature.pokemonlist

import androidx.lifecycle.viewModelScope
import com.example.pokedex.core.mvi.BaseViewModel
import com.example.pokedex.domain.model.Pokemon
import com.example.pokedex.domain.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel

/**
 * This class is responsible for PokemonListViewModel logic.
 * Part of the Clean Architecture structure.
 */
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository
) : BaseViewModel<PokemonListState, PokemonListEvent, PokemonListEffect>() {

    override fun createInitialState(): PokemonListState = PokemonListState()

    init {
        setEvent(PokemonListEvent.LoadPokemon)
    }

    private var searchJob: kotlinx.coroutines.Job? = null

    override fun handleEvent(event: PokemonListEvent) {
        when (event) {
            is PokemonListEvent.LoadPokemon -> loadPokemon()
            is PokemonListEvent.LoadNextPage -> loadNextPage()
            is PokemonListEvent.OnPokemonClicked -> {
                setEffect { PokemonListEffect.NavigateToDetail(event.pokemonId) }
            }
            is PokemonListEvent.OnSearchQueryChanged -> {
                setState { copy(searchQuery = event.query) }
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(300)
                    loadPokemon()
                }
            }
            is PokemonListEvent.OnTypeFilterSelected -> {
                setState { copy(selectedType = event.type) }
                applyFilters()
            }
        }
    }

    private fun loadPokemon() {
        val query = uiState.value.searchQuery
        viewModelScope.launch {
            setState { copy(isLoading = true, errorMessage = null, offset = 0) }
            
            val fetcher = if (query.isNotBlank()) {
                repository.searchPokemon(query, limit = 20, offset = 0)
            } else {
                repository.getPokemonList(limit = 20, offset = 0)
            }
            
            fetcher.fold(
                onSuccess = { list ->
                    setState { 
                        copy(
                            isLoading = false, 
                            pokemonList = list, 
                            offset = list.size,
                            isEndReached = list.isEmpty() || list.size < 20
                        ) 
                    }
                    applyFilters()
                },
                onFailure = { error ->
                    setState { copy(isLoading = false, errorMessage = com.example.pokedex.core.R.string.error_default) }
                }
            )
        }
    }

    private fun loadNextPage() {
        val currentState = uiState.value
        if (currentState.isLoading || currentState.isFetchingNextPage || currentState.isEndReached) {
            return
        }

        viewModelScope.launch {
            setState { copy(isFetchingNextPage = true) }

            val query = currentState.searchQuery
            val fetcher = if (query.isNotBlank()) {
                repository.searchPokemon(query, limit = 20, offset = currentState.offset)
            } else {
                repository.getPokemonList(limit = 20, offset = currentState.offset)
            }

            fetcher.fold(
                onSuccess = { list ->
                    setState {
                        copy(
                            isFetchingNextPage = false,
                            pokemonList = currentState.pokemonList + list,
                            offset = currentState.offset + list.size,
                            isEndReached = list.isEmpty() || list.size < 20
                        )
                    }
                    applyFilters()
                },
                onFailure = { error ->
                    setState { 
                        copy(
                            isFetchingNextPage = false,
                            errorMessage = com.example.pokedex.core.R.string.error_default
                        ) 
                    }
                }
            )
        }
    }
    
    private fun applyFilters() {
        setState {
            val query = searchQuery.trim().lowercase()
            val filtered = pokemonList.filter { pokemon ->
                val matchesQuery = if (query.isNotEmpty()) {
                    pokemon.name.lowercase().contains(query) || pokemon.id.toString() == query
                } else true
                
                val matchesType = if (selectedType != null) {
                    pokemon.types.any { it.equals(selectedType, ignoreCase = true) }
                } else true
                
                matchesQuery && matchesType
            }
            copy(filteredPokemonList = filtered)
        }
    }
}
