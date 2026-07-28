package com.example.pokedex.feature.favorite.impl.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pokedex.core.designsystem.components.PokemonCard
import com.example.pokedex.theme.LocalDimensions

/**
 * Displays favorite Pokémon from [FavoriteViewModel] in an adaptive grid.
 *
 * @param onBackClick callback for the top-app-bar navigation action.
 * @param onNavigateToDetail callback receiving the selected Pokémon ID.
 * @param viewModel Hilt-provided state holder.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    onBackClick: () -> Unit,
    onNavigateToDetail: (Int) -> Unit,
    viewModel: FavoriteViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("Preferiti") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                scrollBehavior = scrollBehavior,
            )
        },
    ) { padding ->
        val dimensions = LocalDimensions.current
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (!uiState.error.isNullOrBlank()) {
                Text(
                    text = uiState.error!!,
                    modifier = Modifier.align(Alignment.Center).padding(padding),
                )
            } else if (uiState.favorites.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.padding(bottom = dimensions.paddingMedium),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "Nessun Pokémon nei preferiti",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = dimensions.gridCellMinSize),
                    contentPadding =
                        PaddingValues(
                            start = dimensions.paddingMedium,
                            top = dimensions.paddingMedium + padding.calculateTopPadding(),
                            end = dimensions.paddingMedium,
                            bottom = dimensions.paddingMedium + padding.calculateBottomPadding(),
                        ),
                    horizontalArrangement = Arrangement.spacedBy(space = dimensions.paddingMedium),
                    verticalArrangement = Arrangement.spacedBy(space = dimensions.paddingMedium),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(
                        items = uiState.favorites,
                        key = { it.id },
                    ) { pokemon ->
                        PokemonCard(
                            pokemon = pokemon,
                            onClick = { onNavigateToDetail(pokemon.id) },
                            onFavoriteClick = { viewModel.toggleFavorite(pokemon) },
                        )
                    }
                }
            }
        }
    }
}
