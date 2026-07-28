package com.example.pokedex.core.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage
import com.example.pokedex.domain.model.Pokemon
import com.example.pokedex.theme.LocalDimensions

/**
 * Displays a Pokémon summary using the shared design-system tokens.
 *
 * The entire card invokes [onClick]. When [pokemon] is a favorite, the favorite icon is actionable
 * only if [onFavoriteClick] is provided; otherwise it is a decorative status indicator.
 *
 * @param pokemon domain model rendered by the card.
 * @param onClick callback for opening the Pokémon detail.
 * @param onFavoriteClick optional callback for removing or changing favorite status.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PokemonCard(
    pokemon: Pokemon,
    onClick: () -> Unit,
    onFavoriteClick: (() -> Unit)? = null,
) {
    val dimensions = LocalDimensions.current
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = dimensions.elevationDefault),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(all = dimensions.paddingSmall),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AsyncImage(
                    model = pokemon.imageUrl,
                    contentDescription = "Immagine di ${pokemon.name}",
                    modifier = Modifier.size(size = dimensions.imageSizeList),
                    placeholder = if (LocalInspectionMode.current) painterResource(id = android.R.drawable.ic_menu_gallery) else null,
                    error = if (LocalInspectionMode.current) painterResource(id = android.R.drawable.ic_menu_gallery) else null,
                )
                Spacer(modifier = Modifier.height(height = dimensions.paddingSmall))
                Text(
                    text = pokemon.name.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(height = dimensions.paddingSmall))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(space = dimensions.cornerRadiusSmall),
                    verticalArrangement = Arrangement.spacedBy(space = dimensions.cornerRadiusSmall),
                ) {
                    pokemon.types.forEach { type ->
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = MaterialTheme.shapes.small,
                        ) {
                            Text(
                                text = type,
                                style = MaterialTheme.typography.labelSmall,
                                modifier =
                                    Modifier.padding(
                                        horizontal = dimensions.paddingSmall,
                                        vertical = dimensions.cornerRadiusSmall,
                                    ),
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                        }
                    }
                }
            }

            if (pokemon.isFavorite) {
                if (onFavoriteClick != null) {
                    IconButton(
                        onClick = onFavoriteClick,
                        modifier = Modifier.align(alignment = Alignment.TopEnd),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(size = dimensions.iconSizeMedium),
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier =
                            Modifier
                                .align(alignment = Alignment.TopEnd)
                                .padding(all = dimensions.paddingSmall)
                                .size(size = dimensions.iconSizeMedium),
                    )
                }
            }
        }
    }
}
