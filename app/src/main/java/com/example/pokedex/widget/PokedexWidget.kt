package com.example.pokedex.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.pokedex.R
import com.example.pokedex.domain.model.Pokemon
import com.example.pokedex.domain.repository.PokemonRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first

/**
 * Glance app widget that renders the first locally stored favorite Pokémon.
 *
 * Data is obtained through a Hilt entry point because Glance widgets are not constructor-injected
 * Android entry points. The current implementation uses a static image and reads only one favorite.
 */
class PokedexWidget : GlanceAppWidget() {
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId,
    ) {
        val repository =
            EntryPointAccessors
                .fromApplication(
                    context.applicationContext,
                    PokedexWidgetEntryPoint::class.java,
                ).pokemonRepository()
        // Recuperiamo il set di preferiti dal database offline
        val favorites = repository.observeFavoritePokemonIds().first()
        val favoritePokemon =
            if (favorites.isNotEmpty()) {
                // Prendiamo il primo preferito per mostrarlo (potrebbe anche essere random)
                repository.getPokemonDetail(favorites.first()).getOrNull()
            } else {
                null
            }

        provideContent {
            GlanceTheme {
                PokedexWidgetContent(favoritePokemon)
            }
        }
    }
}

/**
 * Renders widget content for [pokemon], or a generic empty state when it is `null`.
 *
 * This function uses Glance composables, which are not interchangeable with regular Compose UI.
 */
@Composable
fun PokedexWidgetContent(pokemon: Pokemon?) {
    Column(
        modifier =
            GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.background)
                .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (pokemon != null) {
            Text(
                text = "Il tuo Preferito",
                style =
                    TextStyle(
                        fontWeight = FontWeight.Bold,
                        color = GlanceTheme.colors.primary,
                    ),
            )
            Spacer(modifier = GlanceModifier.height(8.dp))

            // Dato che Glance non supporta nativamente Coil/Glide caricati da URL asincrono
            // direttamente tramite composable standard in modo semplice,
            // per ora mostriamo un'icona statica di pokeball e il nome.
            // Per immagini da URL serve un worker che le scarica e le salva in locale, o un ImageProvider(Bitmap).
            Image(
                provider = ImageProvider(R.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier = GlanceModifier.size(48.dp),
            )

            Spacer(modifier = GlanceModifier.height(8.dp))
            Text(
                text = pokemon.name.replaceFirstChar { it.uppercase() },
                style =
                    TextStyle(
                        fontWeight = FontWeight.Bold,
                        color = GlanceTheme.colors.onBackground,
                    ),
            )
        } else {
            Image(
                provider = ImageProvider(R.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier = GlanceModifier.size(48.dp),
            )
            Spacer(modifier = GlanceModifier.height(8.dp))
            Text(
                text = "Pokedex",
                style =
                    TextStyle(
                        fontWeight = FontWeight.Bold,
                        color = GlanceTheme.colors.primary,
                    ),
            )
            Text(
                text = "Catturali tutti!",
                style =
                    TextStyle(
                        color = GlanceTheme.colors.onBackground,
                    ),
                modifier = GlanceModifier.padding(top = 4.dp),
            )
        }
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
private interface PokedexWidgetEntryPoint {
    fun pokemonRepository(): PokemonRepository
}
