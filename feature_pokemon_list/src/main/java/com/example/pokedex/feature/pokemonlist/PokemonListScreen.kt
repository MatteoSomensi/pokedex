package com.example.pokedex.feature.pokemonlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import com.example.pokedex.core.designsystem.components.PokemonCard
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.pokedex.core.R
import com.example.pokedex.core.ui.DevicePreviews
import com.example.pokedex.core.util.Constants
import com.example.pokedex.domain.model.Pokemon
import com.example.pokedex.theme.LocalDimensions
import com.example.pokedex.theme.LocalWeights
import com.example.pokedex.theme.PokedexTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonListScreen(
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    viewModel: PokemonListViewModel = hiltViewModel<PokemonListViewModel>()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val pagedPokemon = viewModel.pagedPokemonFlow.collectAsLazyPagingItems()

    LaunchedEffect(key1 = viewModel.uiEffect) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is PokemonListEffect.NavigateToDetail -> onNavigateToDetail(effect.pokemonId)
                is PokemonListEffect.NavigateToProfile -> onNavigateToProfile()
                is PokemonListEffect.NavigateToFavorites -> onNavigateToFavorites()
            }
        }
    }

    PokemonListScreenContent(
        state = state,
        pagedPokemon = pagedPokemon,
        onEvent = viewModel::setEvent,
        onNavigateToProfile = onNavigateToProfile,
        onNavigateToFavorites = onNavigateToFavorites
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonListScreenContent(
    state: PokemonListState,
    pagedPokemon: LazyPagingItems<Pokemon>,
    onEvent: (PokemonListEvent) -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToFavorites: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                actions = {
                    IconButton(onClick = onNavigateToFavorites) {
                        Icon(imageVector = Icons.Default.Favorite, contentDescription = "Preferiti")
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Profile")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val dimensions = LocalDimensions.current
            val weights = LocalWeights.current

            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { onEvent(PokemonListEvent.OnSearchQueryChanged(query = it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = paddingValues.calculateTopPadding())
                    .padding(
                        horizontal = dimensions.paddingMedium,
                        vertical = dimensions.paddingSmall
                    ),
                placeholder = { Text(text = stringResource(id = R.string.search_hint)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            val types = state.availableTypes

            LazyRow(
                contentPadding = PaddingValues(horizontal = dimensions.paddingMedium),
                horizontalArrangement = Arrangement.spacedBy(space = dimensions.paddingSmall)
            ) {
                item {
                    FilterChip(
                        selected = state.selectedType == null,
                        onClick = {
                            onEvent(PokemonListEvent.OnTypeFilterSelected(type = null))
                        },
                        label = { Text(text = stringResource(id = R.string.filter_all)) }
                    )
                }
                items(types.size) { index ->
                    val type = types[index]
                    FilterChip(
                        selected = state.selectedType == type,
                        onClick = { onEvent(PokemonListEvent.OnTypeFilterSelected(type = type)) },
                        label = { Text(text = type) }
                    )
                }
            }

            androidx.compose.material3.pulltorefresh.PullToRefreshBox(
                isRefreshing = pagedPokemon.loadState.refresh is LoadState.Loading,
                onRefresh = { pagedPokemon.refresh() },
                modifier = Modifier
                    .fillMaxSize()
                    .weight(weight = weights.listContentWeight)
            ) {
                when {
                    pagedPokemon.loadState.refresh is LoadState.Error -> {
                        val error = (pagedPokemon.loadState.refresh as LoadState.Error).error
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Errore: ${error.localizedMessage}")
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { pagedPokemon.retry() }) {
                                Text("Riprova")
                            }
                        }
                    }

                    pagedPokemon.itemCount == 0 && pagedPokemon.loadState.refresh is LoadState.NotLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Nessun Pokémon trovato con i filtri attuali.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    else -> {
                        val dimensions = LocalDimensions.current
                        val gridState = rememberLazyGridState()

                        LazyVerticalGrid(
                            state = gridState,
                            columns = GridCells.Adaptive(minSize = dimensions.gridCellMinSize),
                            contentPadding = PaddingValues(
                                start = dimensions.paddingMedium,
                                top = dimensions.paddingMedium,
                                end = dimensions.paddingMedium,
                                bottom = dimensions.paddingMedium + paddingValues.calculateBottomPadding()
                            ),
                            horizontalArrangement = Arrangement.spacedBy(space = dimensions.paddingMedium),
                            verticalArrangement = Arrangement.spacedBy(space = dimensions.paddingMedium),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(count = pagedPokemon.itemCount, key = { index -> 
                                val id = pagedPokemon.peek(index)?.id ?: index
                                id 
                            }) { index ->
                                val pokemon = pagedPokemon[index]
                                if (pokemon != null) {
                                    PokemonCard(
                                        pokemon = pokemon,
                                        onClick = { onEvent(PokemonListEvent.OnPokemonClicked(pokemonId = pokemon.id)) }
                                    )
                                }
                            }

                            if (pagedPokemon.loadState.append is LoadState.Loading) {
                                item(span = { GridItemSpan(currentLineSpan = maxLineSpan) }) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(all = dimensions.paddingMedium),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            } else if (pagedPokemon.loadState.append is LoadState.Error) {
                                val error = (pagedPokemon.loadState.append as LoadState.Error).error
                                item(span = { GridItemSpan(currentLineSpan = maxLineSpan) }) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(all = dimensions.paddingMedium),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = error.localizedMessage ?: "Errore di caricamento",
                                            color = MaterialTheme.colorScheme.error
                                        )
                                        Spacer(modifier = Modifier.height(dimensions.paddingSmall))
                                        Button(onClick = { pagedPokemon.retry() }) {
                                            Text(stringResource(id = R.string.retry))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


/**
 * This class is responsible for PokemonListScreen logic.
 * Part of the Clean Architecture structure.
 */
class PokemonPreviewProvider : PreviewParameterProvider<Pokemon> {
    override val values = sequenceOf(
        Pokemon(
            id = 1,
            name = "Bulbasaur",
            imageUrl = "${Constants.POKE_IMAGE_BASE_URL}1.png",
            cryUrl = "",
            types = listOf("Grass", "Poison")
        )
    )
}

@DevicePreviews
@Composable
fun PokemonCardPreview(@PreviewParameter(PokemonPreviewProvider::class) pokemon: Pokemon) {
    PokedexTheme {
        Surface {
            PokemonCard(pokemon = pokemon, onClick = {})
        }
    }
}

            // Previews of Paging3 are complex, omitting dummy LazyPagingItems creation for simplicity
            // in a real setup we would mock the flow or use a fake Pager.
            // PokemonListScreenContent(state = mockState, pagedPokemon = ..., onEvent = {}, ...)
