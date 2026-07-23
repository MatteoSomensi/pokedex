package com.example.pokedex.feature.pokemonlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.pokedex.domain.model.Pokemon
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.pokedex.core.ui.DevicePreviews
import com.example.pokedex.core.R
import com.example.pokedex.theme.LocalDimensions
import com.example.pokedex.theme.PokedexTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonListScreen(
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: PokemonListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.uiEffect) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is PokemonListEffect.NavigateToDetail -> onNavigateToDetail(effect.pokemonId)
            }
        }
    }

    PokemonListScreenContent(
        state = state,
        onEvent = viewModel::setEvent,
        onNavigateToProfile = onNavigateToProfile
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonListScreenContent(
    state: PokemonListState,
    onEvent: (PokemonListEvent) -> Unit,
    onNavigateToProfile: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
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
                .padding(paddingValues)
        ) {
            val dimensions = LocalDimensions.current

            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { onEvent(PokemonListEvent.OnSearchQueryChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensions.paddingMedium, vertical = dimensions.paddingSmall),
                placeholder = { Text(stringResource(R.string.search_hint)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            val types = listOf("Grass", "Fire", "Water", "Bug", "Normal", "Poison", "Electric", "Ground", "Fairy", "Fighting", "Psychic", "Rock", "Ghost", "Ice", "Dragon", "Flying")
            
            androidx.compose.foundation.lazy.LazyRow(
                contentPadding = PaddingValues(horizontal = dimensions.paddingMedium),
                horizontalArrangement = Arrangement.spacedBy(dimensions.paddingSmall)
            ) {
                item {
                    FilterChip(
                        selected = state.selectedType == null,
                        onClick = { onEvent(PokemonListEvent.OnTypeFilterSelected(null)) },
                        label = { Text("All") }
                    )
                }
                items(types.size) { index ->
                    val type = types[index]
                    FilterChip(
                        selected = state.selectedType == type,
                        onClick = { onEvent(PokemonListEvent.OnTypeFilterSelected(type)) },
                        label = { Text(type) }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.errorMessage != null -> {
                    Text(
                        text = state.errorMessage ?: stringResource(R.string.error_default),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    val dimensions = LocalDimensions.current
                    val gridState = rememberLazyGridState()

                    val shouldLoadMore by remember {
                        derivedStateOf {
                            val lastVisibleItem = gridState.layoutInfo.visibleItemsInfo.lastOrNull()
                            lastVisibleItem != null && lastVisibleItem.index >= gridState.layoutInfo.totalItemsCount - 5
                        }
                    }

                    LaunchedEffect(shouldLoadMore) {
                        if (shouldLoadMore && !state.isFetchingNextPage && !state.isEndReached) {
                            onEvent(PokemonListEvent.LoadNextPage)
                        }
                    }

                    LazyVerticalGrid(
                        state = gridState,
                        columns = GridCells.Adaptive(minSize = 150.dp),
                        contentPadding = PaddingValues(dimensions.paddingMedium),
                        horizontalArrangement = Arrangement.spacedBy(dimensions.paddingMedium),
                        verticalArrangement = Arrangement.spacedBy(dimensions.paddingMedium),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.filteredPokemonList, key = { it.id }) { pokemon ->
                            PokemonCard(
                                pokemon = pokemon,
                                onClick = { onEvent(PokemonListEvent.OnPokemonClicked(pokemon.id)) }
                            )
                        }
                        
                        if (state.isFetchingNextPage) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(dimensions.paddingMedium),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PokemonCard(pokemon: Pokemon, onClick: () -> Unit) {
    val dimensions = LocalDimensions.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = dimensions.elevationDefault)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.paddingSmall),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = pokemon.imageUrl,
                contentDescription = stringResource(id = R.string.cd_pokemon_image, pokemon.name),
                modifier = Modifier.size(dimensions.imageSizeList)
            )
            Spacer(modifier = Modifier.height(dimensions.paddingSmall))
            Text(
                text = pokemon.name.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(dimensions.paddingSmall))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(dimensions.cornerRadiusSmall),
                verticalArrangement = Arrangement.spacedBy(dimensions.cornerRadiusSmall)
            ) {
                pokemon.types.forEach { type ->
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = type,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = dimensions.paddingSmall, vertical = dimensions.cornerRadiusSmall),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
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
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png",
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

@DevicePreviews
@Composable
fun PokemonListScreenPreview() {
    val mockState = PokemonListState(
        isLoading = false,
        pokemonList = listOf(
            Pokemon(1, "Bulbasaur", "", listOf("Grass", "Poison")),
            Pokemon(4, "Charmander", "", listOf("Fire")),
            Pokemon(7, "Squirtle", "", listOf("Water"))
        )
    )
    PokedexTheme {
        Surface {
            PokemonListScreenContent(state = mockState, onEvent = {}, onNavigateToProfile = {})
        }
    }
}
