package com.example.pokedex

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.material3.Text
import com.example.pokedex.core.R
import com.example.pokedex.feature.pokemonlist.PokemonListScreen
import com.example.pokedex.feature.pokemondetail.PokemonDetailScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import com.example.pokedex.feature.auth.AuthRoute
import com.example.pokedex.feature.auth.profile.ProfileScreen
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun MainNavigation() {
  val startDestination = if (FirebaseAuth.getInstance().currentUser != null) PokemonList else Auth
  val backStack = rememberNavBackStack(startDestination)
  val listDetailStrategy = rememberListDetailSceneStrategy<androidx.navigation3.runtime.NavKey>()

  NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    sceneStrategies = listOf(listDetailStrategy),
    entryProvider =
      entryProvider {
        entry<PokemonList>(
            metadata = ListDetailSceneStrategy.listPane(
                detailPlaceholder = {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(androidx.compose.ui.res.stringResource(R.string.list_detail_placeholder))
                    }
                }
            )
        ) {
          PokemonListScreen(
              onNavigateToDetail = { pokemonId -> backStack.add(PokemonDetail(pokemonId)) },
              onNavigateToProfile = { backStack.add(Profile) }
          )
        }
        entry<PokemonDetail>(
            metadata = ListDetailSceneStrategy.detailPane()
        ) {
            com.example.pokedex.feature.pokemondetail.PokemonDetailScreen(
                pokemonId = it.id,
                onBackClick = { backStack.removeLastOrNull() }
            )
        }
        entry<Auth> {
            val context = androidx.compose.ui.platform.LocalContext.current
            val webClientId = androidx.compose.runtime.remember(context) {
                val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
                val fromRes = if (resId != 0) context.getString(resId) else ""
                fromRes.ifBlank { com.example.pokedex.BuildConfig.WEB_CLIENT_ID }
            }
            AuthRoute(
                webClientId = webClientId,
                onAuthSuccess = {
                    backStack.clear()
                    backStack.add(PokemonList)
                }
            )
        }
        entry<Profile> {
            ProfileScreen(
                onNavigateBack = { backStack.removeLastOrNull() },
                onNavigateToAuth = {
                    backStack.clear()
                    backStack.add(Auth)
                }
            )
        }
      },
  )
}
