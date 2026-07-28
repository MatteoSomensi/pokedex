package com.example.pokedex.feature.pokemonlist

import androidx.lifecycle.viewModelScope
import com.example.pokedex.core.R
import com.example.pokedex.core.mvi.BaseViewModel
import com.example.pokedex.core.util.UiText
import com.example.pokedex.domain.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel

/**
 * ViewModel managing the state and logic for the Pokemon List screen.
 * Handles loading paginated lists of Pokemon, filtering by type, and searching by name or ID.
 * Manages UI events and triggers corresponding effects such as navigation.
 */
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository
) : BaseViewModel<PokemonListState, PokemonListEvent, PokemonListEffect>() {

    override fun createInitialState(): PokemonListState = PokemonListState()

    init {
        loadTypes()
        setEvent(event = PokemonListEvent.LoadPokemon)
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            repository.observeFavoritePokemonIds().collect { favoriteIds ->
                setState { 
                    val updatedList = this.pokemonList.map { it.copy(isFavorite = favoriteIds.contains(it.id)) }
                    copy(pokemonList = updatedList) 
                }
                applyFilters()
            }
        }
    }

    private fun loadTypes() {
        viewModelScope.launch {
            repository.getPokemonTypes().onSuccess { types ->
                setState { copy(availableTypes = types) }
            }
        }
    }

    private var searchJob: Job? = null

    override fun handleEvent(event: PokemonListEvent) {
        when (event) {
            is PokemonListEvent.LoadPokemon -> loadPokemon(isRefresh = false)
            is PokemonListEvent.Refresh -> loadPokemon(isRefresh = true)
            is PokemonListEvent.LoadNextPage -> loadNextPage()
            is PokemonListEvent.OnPokemonClicked -> {
                setEffect { PokemonListEffect.NavigateToDetail(pokemonId = event.pokemonId) }
            }

            is PokemonListEvent.OnSearchQueryChanged -> {
                setState { copy(searchQuery = event.query) }
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(duration = SEARCH_DEBOUNCE)
                    loadPokemon(isRefresh = false)
                }
            }

            is PokemonListEvent.OnTypeFilterSelected -> {
                setState { copy(selectedType = event.type) }
                applyFilters()
            }
        }
    }

    private fun loadPokemon(isRefresh: Boolean = false) {
        val query = uiState.value.searchQuery
        viewModelScope.launch {
            setState {
                copy(
                    isLoading = !isRefresh,
                    isRefreshing = isRefresh,
                    errorMessage = null,
                    nextPageError = null,
                    offset = 0
                )
            }

            val fetcher = if (query.isNotBlank()) {
                repository.searchPokemon(query = query, limit = PAGE_SIZE, offset = 0)
            } else {
                repository.getPokemonList(limit = PAGE_SIZE, offset = 0, forceRefresh = isRefresh)
            }

            fetcher.fold(
                onSuccess = { list ->
                    setState {
                        copy(
                            isLoading = false,
                            isRefreshing = false,
                            pokemonList = list,
                            offset = list.size,
                            isEndReached = list.isEmpty() || list.size < PAGE_SIZE
                        )
                    }
                    applyFilters()
                },
                onFailure = { error ->
                    setState {
                        copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = UiText.StringResource(id = R.string.error_default)
                        )
                    }
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
            setState { copy(isFetchingNextPage = true, nextPageError = null) }

            val query = currentState.searchQuery
            val fetcher = if (query.isNotBlank()) {
                repository.searchPokemon(
                    query = query,
                    limit = PAGE_SIZE,
                    offset = currentState.offset
                )
            } else {
                repository.getPokemonList(limit = PAGE_SIZE, offset = currentState.offset)
            }

            fetcher.fold(
                onSuccess = { list ->
                    setState {
                        copy(
                            isFetchingNextPage = false,
                            pokemonList = currentState.pokemonList + list,
                            offset = currentState.offset + list.size,
                            isEndReached = list.isEmpty() || list.size < PAGE_SIZE
                        )
                    }
                    applyFilters()
                },
                onFailure = {
                    setState {
                        copy(
                            isFetchingNextPage = false,
                            nextPageError = UiText.StringResource(id = R.string.error_default)
                        )
                    }
                }
            )
        }
    }

    private var filterJob: Job? = null

    private fun applyFilters() {
        filterJob?.cancel()
        filterJob = viewModelScope.launch {
            val state = uiState.value
            
            val baseList = state.pokemonList

            val query = state.searchQuery.trim().lowercase()
            val filtered = baseList.filter { pokemon ->
                val matchesQuery = if (query.isNotEmpty()) {
                    pokemon.name.lowercase()
                        .contains(other = query) || pokemon.id.toString() == query
                } else true

                val matchesType = if (state.selectedType != null) {
                    pokemon.types.any { it.equals(other = state.selectedType, ignoreCase = true) }
                } else true

                matchesQuery && matchesType
            }
            setState { copy(filteredPokemonList = filtered) }
        }
    }

    companion object {
        private const val PAGE_SIZE = 20
        private val SEARCH_DEBOUNCE = 300.milliseconds
    }
}
