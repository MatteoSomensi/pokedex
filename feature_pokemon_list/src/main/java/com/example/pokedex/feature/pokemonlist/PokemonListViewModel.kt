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

    override fun handleEvent(event: PokemonListEvent) {
        when (event) {
            is PokemonListEvent.LoadPokemon -> loadPokemon()
            is PokemonListEvent.LoadNextPage -> loadNextPage()
            is PokemonListEvent.OnPokemonClicked -> {
                setEffect { PokemonListEffect.NavigateToDetail(event.pokemonId) }
            }
            is PokemonListEvent.OnSearchQueryChanged -> {
                setState { copy(searchQuery = event.query) }
                applyFilters()
            }
            is PokemonListEvent.OnTypeFilterSelected -> {
                setState { copy(selectedType = event.type) }
                applyFilters()
            }
        }
    }

    private fun loadPokemon() {
        if (uiState.value.isLoading) return
        viewModelScope.launch {
            setState { copy(isLoading = true, errorMessage = null, offset = 0) }
            
            repository.getPokemonList(limit = 20, offset = 0).fold(
                onSuccess = { list ->
                    setState { 
                        copy(
                            isLoading = false, 
                            pokemonList = list, 
                            offset = 20,
                            isEndReached = list.isEmpty()
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

            repository.getPokemonList(limit = 20, offset = currentState.offset).fold(
                onSuccess = { list ->
                    setState {
                        copy(
                            isFetchingNextPage = false,
                            pokemonList = currentState.pokemonList + list,
                            offset = currentState.offset + list.size,
                            isEndReached = list.isEmpty()
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
