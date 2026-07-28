package com.example.pokedex.feature.pokemonlist

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
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
import kotlin.time.Duration.Companion.milliseconds

/**
 * Produces debounced, query-aware Paging data and list control state.
 *
 * Search changes replace the active repository flow through `flatMapLatest`. Type filtering is
 * applied to the resulting pages, and the stream is cached in [viewModelScope].
 */
@HiltViewModel
class PokemonListViewModel
    @Inject
    constructor(
        private val repository: PokemonRepository,
    ) : BaseViewModel<PokemonListState, PokemonListEvent, PokemonListEffect>() {
        override fun createInitialState(): PokemonListState = PokemonListState()

        init {
            loadTypes()
        }

        /** Paging stream derived from the current search query and selected type. */
        @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
        val pagedPokemonFlow: Flow<PagingData<Pokemon>> =
            combine(
                uiState
                    .map { it.searchQuery.trim() }
                    .distinctUntilChanged()
                    .debounce(timeout = SEARCH_DEBOUNCE),
                uiState
                    .map { it.selectedType }
                    .distinctUntilChanged(),
            ) { query, selectedType ->
                query to selectedType
            }.flatMapLatest { (query, selectedType) ->
                repository.getPokemonPaged(query = query).map { pagingData ->
                    if (selectedType != null) {
                        pagingData.filter { pokemon ->
                            pokemon.types.any { it.equals(other = selectedType, ignoreCase = true) }
                        }
                    } else {
                        pagingData
                    }
                }
            }.cachedIn(viewModelScope)

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
            val SEARCH_DEBOUNCE = 300.milliseconds
        }
    }
