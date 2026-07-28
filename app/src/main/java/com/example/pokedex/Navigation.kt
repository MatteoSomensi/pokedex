package com.example.pokedex

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.LocalListDetailSceneScope
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.pokedex.core.R
import com.example.pokedex.feature.auth.AuthRoute
import com.example.pokedex.feature.auth.profile.ProfileScreen
import com.example.pokedex.feature.favorite.api.Favorite
import com.example.pokedex.feature.favorite.impl.ui.FavoriteScreen
import com.example.pokedex.feature.pokemondetail.PokemonDetailScreen
import com.example.pokedex.feature.pokemonlist.PokemonListScreen
import com.google.firebase.auth.FirebaseAuth

private data object PokedexListDetailScene

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun MainNavigation() {
    val startDestination = if (FirebaseAuth.getInstance().currentUser != null) PokemonList else Auth
    val backStack = rememberNavBackStack(startDestination)
    val windowAdaptiveInfo = currentWindowAdaptiveInfoV2()
    val paneScaffoldDirective = remember(windowAdaptiveInfo) {
        calculatePaneScaffoldDirective(windowAdaptiveInfo)
            .copy(horizontalPartitionSpacerSize = 0.dp)
    }
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>(
        shouldHandleSinglePaneLayout = false,
        directive = paneScaffoldDirective
    )

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        sceneStrategies = listOf(listDetailStrategy),
        entryProvider =
            entryProvider {
                entry<PokemonList>(
                    metadata = ListDetailSceneStrategy.listPane(
                        sceneKey = PokedexListDetailScene,
                        detailPlaceholder = { PokemonDetailPlaceholder() }
                    )
                ) {
                    PokemonListScreen(
                        onNavigateToDetail = { pokemonId -> 
                            if (backStack.lastOrNull() !is PokemonDetail) {
                                backStack.add(PokemonDetail(id = pokemonId)) 
                            }
                        },
                        onNavigateToProfile = { 
                            if (backStack.lastOrNull() != Profile) {
                                backStack.add(element = Profile) 
                            }
                        },
                        onNavigateToFavorites = { 
                            if (backStack.lastOrNull() != Favorite) {
                                backStack.add(Favorite) 
                            }
                        }
                    )
                }
                entry<PokemonDetail>(
                    metadata = ListDetailSceneStrategy.detailPane(
                        sceneKey = PokedexListDetailScene
                    )
                ) {
                    PokemonDetailScreen(
                        pokemonId = it.id,
                        onBackClick = { backStack.removeLastOrNull() },
                        showBackButton = LocalListDetailSceneScope.current == null
                    )
                }
                entry<Favorite>(
                    metadata = ListDetailSceneStrategy.listPane(
                        sceneKey = PokedexListDetailScene,
                        detailPlaceholder = { PokemonDetailPlaceholder() }
                    )
                ) {
                    FavoriteScreen(
                        onBackClick = { backStack.removeLastOrNull() },
                        onNavigateToDetail = { pokemonId -> 
                            if (backStack.lastOrNull() !is PokemonDetail) {
                                backStack.add(PokemonDetail(id = pokemonId)) 
                            }
                        }
                    )
                }
                entry<Auth> {
                    AuthRoute(
                        onAuthSuccess = {
                            backStack.clear()
                            backStack.add(element = PokemonList)
                        }
                    )
                }
                entry<Profile> {
                    ProfileScreen(
                        onNavigateBack = { backStack.removeLastOrNull() },
                        onNavigateToAuth = {
                            backStack.clear()
                            backStack.add(element = Auth)
                        }
                    )
                }
            },
    )
}

@Composable
private fun PokemonDetailPlaceholder() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = stringResource(id = R.string.list_detail_placeholder))
    }
}
