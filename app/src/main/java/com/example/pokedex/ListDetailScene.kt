package com.example.pokedex

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.Text
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.contains
import androidx.navigation3.runtime.metadata
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import com.example.pokedex.core.designsystem.LocalBackButtonVisibility

data class ListDetailScene<T : Any>(
    override val key: Any,
    override val previousEntries: List<NavEntry<T>>,
    val listEntry: NavEntry<T>,
    val detailEntry: NavEntry<T>?,
) : Scene<T> {
    override val entries: List<NavEntry<T>> = listOfNotNull(listEntry, detailEntry)
    override val content: @Composable (() -> Unit) = {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.weight(0.4f)) {
                listEntry.Content()
            }

            CompositionLocalProvider(LocalBackButtonVisibility provides false) {
                Column(modifier = Modifier.weight(0.6f)) {
                    if (detailEntry != null) {
                        AnimatedContent(
                            targetState = detailEntry,
                            contentKey = { entry -> entry.contentKey },
                            transitionSpec = {
                                slideInHorizontally(
                                    initialOffsetX = { it }
                                ) togetherWith
                                        slideOutHorizontally(targetOffsetX = { -it })
                            }
                        ) { entry ->
                            entry.Content()
                        }
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Seleziona un Pokémon per visualizzare i dettagli.")
                        }
                    }
                }
            }
        }
    }

    companion object {
        fun listPane() = metadata {
            put(ListKey, true)
        }

        fun detailPane() = metadata {
            put(DetailKey, true)
        }
    }

    object ListKey : NavMetadataKey<Boolean>
    object DetailKey : NavMetadataKey<Boolean>
}

@Composable
fun <T : Any> rememberListDetailSceneStrategy(): ListDetailSceneStrategy<T> {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp

    return remember(screenWidthDp) {
        ListDetailSceneStrategy(screenWidthDp)
    }
}

class ListDetailSceneStrategy<T : Any>(val screenWidthDp: Int) : SceneStrategy<T> {

    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {

        if (screenWidthDp < 450) {
            return null
        }

        val topEntry = entries.lastOrNull() ?: return null
        val isTopList = topEntry.metadata.contains(ListDetailScene.ListKey)
        val isTopDetail = topEntry.metadata.contains(ListDetailScene.DetailKey)

        if (!isTopList && !isTopDetail) {
            return null
        }

        val detailEntry = if (isTopDetail) topEntry else null

        val listEntry =
            entries.findLast { it.metadata.contains(ListDetailScene.ListKey) } ?: return null

        val sceneKey = listEntry.contentKey

        return ListDetailScene(
            key = sceneKey,
            previousEntries = entries.dropLast(1),
            listEntry = listEntry,
            detailEntry = detailEntry
        )
    }
}
