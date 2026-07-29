package com.example.pokedex.widget

import android.content.Context
import androidx.compose.runtime.Composable
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
import com.example.pokedex.theme.LocalDimensions
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
        val favorites = repository.observeFavoritePokemonIds().first()
        val favoritePokemon =
            if (favorites.isNotEmpty()) {
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
 * Since Glance doesn't natively support Coil/Glide for asynchronous URLs directly via standard
 * composables easily, we show a static pokeball icon and the name for now. For URL images, a
 * worker is needed to download and save them locally, or use an ImageProvider(Bitmap).
 */
@Composable
fun PokedexWidgetContent(pokemon: Pokemon?) {
    val dimensions = LocalDimensions.current
    val context = androidx.glance.LocalContext.current
    Column(
        modifier =
            GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.background)
                .padding(dimensions.paddingMedium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (pokemon != null) {
            Text(
                text = context.getString(R.string.widget_favorite_title),
                style =
                    TextStyle(
                        fontWeight = FontWeight.Bold,
                        color = GlanceTheme.colors.primary,
                    ),
            )
            Spacer(modifier = GlanceModifier.height(dimensions.paddingSmall))

            Image(
                provider = ImageProvider(R.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier = GlanceModifier.size(dimensions.iconSizeLarge),
            )

            Spacer(modifier = GlanceModifier.height(dimensions.paddingSmall))
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
                modifier = GlanceModifier.size(dimensions.iconSizeLarge),
            )
            Spacer(modifier = GlanceModifier.height(dimensions.paddingSmall))
            Text(
                text = context.getString(R.string.widget_default_title),
                style =
                    TextStyle(
                        fontWeight = FontWeight.Bold,
                        color = GlanceTheme.colors.primary,
                    ),
            )
            Text(
                text = context.getString(R.string.widget_default_subtitle),
                style =
                    TextStyle(
                        color = GlanceTheme.colors.onBackground,
                    ),
                modifier = GlanceModifier.padding(top = dimensions.paddingExtraSmall),
            )
        }
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
private interface PokedexWidgetEntryPoint {
    fun pokemonRepository(): PokemonRepository
}
