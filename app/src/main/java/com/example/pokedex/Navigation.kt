package com.example.pokedex

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.LocalListDetailSceneScope
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

private data object PokedexListDetailScene

/**
 * Hosts authentication-aware and adaptive root navigation.
 *
 * The function owns the Navigation 3 back stack, switches between single-pane and list-detail
 * scenes based on the current window, and consumes valid `pokedex://pokemon/{id}` deep links.
 *
 * @param deepLinkUri most recent unconsumed URI delivered by the activity.
 * @param onDeepLinkConsumed callback invoked after a URI has been handled or rejected.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@SuppressWarnings("CyclomaticComplexMethod", "LongMethod", "FunctionNaming")
@Composable
fun MainNavigation(
    deepLinkUri: Uri? = null,
    onDeepLinkConsumed: () -> Unit = {},
    viewModel: NavigationViewModel = hiltViewModel(),
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val startDestination =
        remember {
            if (currentUser != null) PokemonList else Auth
        }
    val backStack = rememberNavBackStack(startDestination)

    LaunchedEffect(currentUser) {
        if (currentUser == null && backStack.lastOrNull() != Auth) {
            backStack.clear()
            backStack.add(Auth)
        } else if (currentUser != null && backStack.lastOrNull() == Auth) {
            backStack.clear()
            backStack.add(PokemonList)
        }
    }

    LaunchedEffect(deepLinkUri, currentUser) {
        deepLinkUri?.let { uri ->
            val pokemonId = uri.toPokemonId()
            if (pokemonId == null) {
                onDeepLinkConsumed()
            } else if (currentUser != null) {
                val destination = PokemonDetail(id = pokemonId)
                if (backStack.lastOrNull() != destination) {
                    backStack.add(destination)
                }
                onDeepLinkConsumed()
            }
        }
    }
    val windowAdaptiveInfo = currentWindowAdaptiveInfoV2()
    val paneScaffoldDirective =
        remember(windowAdaptiveInfo) {
            calculatePaneScaffoldDirective(windowAdaptiveInfo)
                .copy(horizontalPartitionSpacerSize = 0.dp)
        }
    val listDetailStrategy =
        rememberListDetailSceneStrategy<NavKey>(
            shouldHandleSinglePaneLayout = false,
            directive = paneScaffoldDirective,
        )

    val isNavigationSuiteVisible = backStack.lastOrNull() != Auth

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            item(
                selected = backStack.lastOrNull() is PokemonList || backStack.lastOrNull() is PokemonDetail,
                onClick = {
                    if (backStack.lastOrNull() !is PokemonList && backStack.lastOrNull() !is PokemonDetail) {
                        backStack.clear()
                        backStack.add(PokemonList)
                    }
                },
                icon = {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = stringResource(id = R.string.navigation_home),
                    )
                },
                label = { Text(stringResource(id = R.string.navigation_home)) },
            )
            item(
                selected = backStack.lastOrNull() is Favorite,
                onClick = {
                    if (backStack.lastOrNull() !is Favorite) {
                        backStack.clear()
                        backStack.add(PokemonList)
                        backStack.add(Favorite)
                    }
                },
                icon = {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = stringResource(id = R.string.navigation_favorites),
                    )
                },
                label = { Text(stringResource(id = R.string.navigation_favorites)) },
            )
            item(
                selected = backStack.lastOrNull() is Profile,
                onClick = {
                    if (backStack.lastOrNull() !is Profile) {
                        backStack.clear()
                        backStack.add(PokemonList)
                        backStack.add(Profile)
                    }
                },
                icon = {
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = stringResource(id = R.string.navigation_profile),
                    )
                },
                label = { Text(stringResource(id = R.string.navigation_profile)) },
            )
        },
        layoutType =
            if (isNavigationSuiteVisible) {
                NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(windowAdaptiveInfo)
            } else {
                NavigationSuiteType.None
            },
    ) {
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            sceneStrategies = listOf(listDetailStrategy),
            entryProvider =
                entryProvider {
                    entry<PokemonList>(
                        metadata =
                            ListDetailSceneStrategy.listPane(
                                sceneKey = PokedexListDetailScene,
                                detailPlaceholder = { PokemonDetailPlaceholder() },
                            ),
                    ) {
                        PokemonListScreen(
                            onNavigateToDetail = { pokemonId ->
                                if (backStack.lastOrNull() != PokemonDetail(id = pokemonId)) {
                                    backStack.add(PokemonDetail(id = pokemonId))
                                }
                            },
                            onNavigateToProfile = {
                                if (backStack.lastOrNull() != Profile) {
                                    backStack.clear()
                                    backStack.add(PokemonList)
                                    backStack.add(element = Profile)
                                }
                            },
                            onNavigateToFavorites = {
                                if (backStack.lastOrNull() != Favorite) {
                                    backStack.clear()
                                    backStack.add(PokemonList)
                                    backStack.add(Favorite)
                                }
                            },
                        )
                    }
                    entry<PokemonDetail>(
                        metadata =
                            ListDetailSceneStrategy.detailPane(
                                sceneKey = PokedexListDetailScene,
                            ),
                    ) {
                        PokemonDetailScreen(
                            pokemonId = it.id,
                            onBackClick = { backStack.removeLastOrNull() },
                            showBackButton = LocalListDetailSceneScope.current == null,
                        )
                    }
                    entry<Favorite>(
                        metadata =
                            ListDetailSceneStrategy.listPane(
                                sceneKey = PokedexListDetailScene,
                                detailPlaceholder = { PokemonDetailPlaceholder() },
                            ),
                    ) {
                        FavoriteScreen(
                            onBackClick = { backStack.removeLastOrNull() },
                            onNavigateToDetail = { pokemonId ->
                                if (backStack.lastOrNull() != PokemonDetail(id = pokemonId)) {
                                    backStack.add(PokemonDetail(id = pokemonId))
                                }
                            },
                        )
                    }
                    entry<Auth> {
                        AuthRoute(
                            onAuthSuccess = {
                                if (backStack.lastOrNull() == Auth) {
                                    backStack.clear()
                                    backStack.add(element = PokemonList)
                                }
                            },
                        )
                    }
                    entry<Profile> {
                        ProfileScreen(
                            onNavigateBack = { backStack.removeLastOrNull() },
                            onNavigateToAuth = {
                                backStack.clear()
                                backStack.add(element = Auth)
                            },
                        )
                    }
                },
        )
    }
}

internal fun Uri.toPokemonId(): Int? {
    if (scheme != "pokedex" || host != "pokemon") return null
    return lastPathSegment?.toIntOrNull()?.takeIf { it > 0 }
}

@Composable
private fun PokemonDetailPlaceholder() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = stringResource(id = R.string.list_detail_placeholder))
    }
}
