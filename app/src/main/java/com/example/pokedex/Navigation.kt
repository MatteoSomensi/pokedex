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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
@SuppressWarnings("CyclomaticComplexMethod", "LongMethod", "FunctionNaming")
@Composable
fun MainNavigation(
    deepLinkUri: android.net.Uri? = null,
    onDeepLinkConsumed: () -> Unit = {},
) {
    val firebaseAuth = remember { FirebaseAuth.getInstance() }
    val startDestination =
        remember {
            if (firebaseAuth.currentUser != null) PokemonList else Auth
        }
    val backStack = rememberNavBackStack(startDestination)

    DisposableEffect(Unit) {
        val listener =
            FirebaseAuth.AuthStateListener { firebaseAuth ->
                if (firebaseAuth.currentUser == null && backStack.lastOrNull() != Auth) {
                    backStack.clear()
                    backStack.add(Auth)
                }
            }
        firebaseAuth.addAuthStateListener(listener)
        onDispose {
            firebaseAuth.removeAuthStateListener(listener)
        }
    }

    LaunchedEffect(deepLinkUri) {
        deepLinkUri?.let { uri ->
            val pokemonId = uri.toPokemonId()
            if (pokemonId == null) {
                onDeepLinkConsumed()
            } else if (firebaseAuth.currentUser != null) {
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

    val isNavBarVisible =
        remember(backStack) {
            val last = backStack.lastOrNull()
            last != null && last != Auth && last != Profile
        }

    androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold(
        navigationSuiteItems = {
            item(
                selected = backStack.lastOrNull() is PokemonList || backStack.lastOrNull() is PokemonDetail,
                onClick = {
                    if (backStack.lastOrNull() !is PokemonList && backStack.lastOrNull() !is PokemonDetail) {
                        backStack.add(PokemonList)
                    }
                },
                icon = {
                    androidx.compose.material3.Icon(
                        androidx.compose.material.icons.Icons.Default.Home,
                        contentDescription = "Home",
                    )
                },
                label = { Text("Home") },
            )
            item(
                selected = backStack.lastOrNull() is Favorite,
                onClick = {
                    if (backStack.lastOrNull() !is Favorite) {
                        backStack.add(Favorite)
                    }
                },
                icon = {
                    androidx.compose.material3.Icon(
                        androidx.compose.material.icons.Icons.Default.Favorite,
                        contentDescription = "Favorites",
                    )
                },
                label = { Text("Favorites") },
            )
            item(
                selected = backStack.lastOrNull() is Profile,
                onClick = {
                    if (backStack.lastOrNull() !is Profile) {
                        backStack.add(Profile)
                    }
                },
                icon = {
                    androidx.compose.material3.Icon(
                        androidx.compose.material.icons.Icons.Default.AccountCircle,
                        contentDescription = "Profile",
                    )
                },
                label = { Text("Profile") },
            )
        },
        layoutType =
            if (isNavBarVisible) {
                androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
                    .calculateFromAdaptiveInfo(windowAdaptiveInfo)
            } else {
                androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType.None
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
                                    backStack.add(element = Profile)
                                }
                            },
                            onNavigateToFavorites = {
                                if (backStack.lastOrNull() != Favorite) {
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
                                backStack.clear()
                                backStack.add(element = PokemonList)
                                deepLinkUri?.toPokemonId()?.let { pokemonId ->
                                    backStack.add(PokemonDetail(id = pokemonId))
                                }
                                if (deepLinkUri != null) {
                                    onDeepLinkConsumed()
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

private fun android.net.Uri.toPokemonId(): Int? {
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
