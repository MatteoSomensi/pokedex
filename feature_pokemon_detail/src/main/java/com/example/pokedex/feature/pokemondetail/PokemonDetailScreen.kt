package com.example.pokedex.feature.pokemondetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.pokedex.core.R
import com.example.pokedex.domain.model.Pokemon
import com.example.pokedex.theme.LocalDimensions
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailScreen(
    pokemonId: Int,
    onBackClick: () -> Unit,
    viewModel: PokemonDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(pokemonId) {
        viewModel.loadPokemon(pokemonId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.pokemon?.name?.replaceFirstChar { it.uppercase() } ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.cd_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.errorMessage != null -> {
                    Text(
                        text = state.errorMessage ?: stringResource(R.string.error_default),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                state.pokemon != null -> {
                    PokemonDetailContent(pokemon = state.pokemon!!)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PokemonDetailContent(pokemon: Pokemon) {
    var isVisible by remember { mutableStateOf(false) }
    val dimensions = LocalDimensions.current

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(dimensions.paddingLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(500)) + slideInVertically(initialOffsetY = { -50 })
        ) {
            AsyncImage(
                model = pokemon.imageUrl,
                contentDescription = stringResource(id = R.string.cd_pokemon_image, pokemon.name),
                modifier = Modifier
                    .size(dimensions.imageSizeDetail)
                    .clip(RoundedCornerShape(dimensions.cornerRadiusLarge))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(dimensions.paddingMedium)
            )
        }

        Spacer(modifier = Modifier.height(dimensions.paddingLarge))

        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(animationSpec = tween(700)) + slideInVertically(initialOffsetY = { 50 })
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = pokemon.name.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(dimensions.paddingMedium))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(dimensions.paddingSmall),
                    verticalArrangement = Arrangement.spacedBy(dimensions.paddingSmall)
                ) {
                    pokemon.types.forEach { type ->
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(dimensions.cornerRadiusExtraLarge)
                        ) {
                            Text(
                                text = type,
                                modifier = Modifier.padding(horizontal = dimensions.paddingMedium, vertical = dimensions.paddingSmall),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(dimensions.paddingExtraLarge))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(dimensions.cornerRadiusExtraLarge),
                    elevation = CardDefaults.cardElevation(defaultElevation = dimensions.elevationLarge),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(dimensions.paddingLarge)
                    ) {
                        Text(
                            text = stringResource(R.string.base_stats),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = dimensions.paddingMedium)
                        )

                        pokemon.stats.forEach { (statName, statValue) ->
                            StatRow(statName = statName, statValue = statValue)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        
                        Divider(modifier = Modifier.padding(vertical = dimensions.paddingMedium))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(stringResource(R.string.height), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(stringResource(R.string.height_format, pokemon.height / 10f), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(stringResource(R.string.weight), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(stringResource(R.string.weight_format, pokemon.weight / 10f), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatRow(statName: String, statValue: Int) {
    val formattedName = statName.replaceFirstChar { it.uppercase() }
    val progress = statValue / 255f
    val dimensions = LocalDimensions.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = formattedName,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(0.3f)
        )
        Text(
            text = statValue.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(dimensions.statValueWidth),
            textAlign = TextAlign.End
        )
        Spacer(modifier = Modifier.width(dimensions.paddingSmall))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .weight(0.7f)
                .height(dimensions.statProgressBarHeight)
                .clip(RoundedCornerShape(dimensions.cornerRadiusSmall)),
            color = if (progress > 0.5f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}


@Preview(name = "Phone - Light", device = "spec:width=411dp,height=891dp", showBackground = true)
@Preview(name = "Phone - Dark", device = "spec:width=411dp,height=891dp", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(name = "Tablet - Light", device = "spec:width=1280dp,height=800dp,dpi=240", showBackground = true)
@Preview(name = "Foldable - Light", device = "spec:width=673dp,height=841dp", showBackground = true)
@Composable
fun PokemonDetailScreenPreview() {
    val mockPokemon = Pokemon(
        id = 1,
        name = "Bulbasaur",
        imageUrl = "",
        types = listOf("Grass", "Poison"),
        height = 7,
        weight = 69,
        stats = mapOf("hp" to 45, "attack" to 49, "defense" to 49, "speed" to 45)
    )
    MaterialTheme {
        Surface {
            PokemonDetailContent(pokemon = mockPokemon)
        }
    }
}
