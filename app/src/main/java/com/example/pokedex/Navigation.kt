package com.example.pokedex

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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

@Composable
fun MainNavigation() {
    val startDestination = if (FirebaseAuth.getInstance().currentUser != null) PokemonList else Auth
    val backStack = rememberNavBackStack(startDestination)
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        sceneStrategies = listOf(listDetailStrategy),
        entryProvider =
            entryProvider {
                entry<PokemonList>(
                    metadata = ListDetailScene.listPane()
                ) {
                    PokemonListScreen(
                        onNavigateToDetail = { pokemonId -> backStack.add(PokemonDetail(id = pokemonId)) },
                        onNavigateToProfile = { backStack.add(element = Profile) },
                        onNavigateToFavorites = { backStack.add(Favorite) }
                    )
                }
                entry<PokemonDetail>(
                    metadata = ListDetailScene.detailPane()
                ) {
                    PokemonDetailScreen(
                        pokemonId = it.id,
                        onBackClick = { backStack.removeLastOrNull() }
                    )
                }
                entry<Favorite> {
                    FavoriteScreen(
                        onBackClick = { backStack.removeLastOrNull() }
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
