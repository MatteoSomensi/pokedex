package com.example.pokedex.feature.pokemondetail

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import coil.compose.AsyncImage
import com.example.pokedex.core.R
import com.example.pokedex.core.ui.DevicePreviews
import com.example.pokedex.domain.model.Pokemon
import com.example.pokedex.domain.model.heightInMeters
import com.example.pokedex.domain.model.weightInKg
import com.example.pokedex.theme.LocalAnimations
import com.example.pokedex.theme.LocalDimensions
import com.example.pokedex.theme.LocalWeights
import com.example.pokedex.theme.PokedexTheme

/**
 * Connects [PokemonDetailViewModel] to detail presentation and Android audio playback.
 *
 * [pokemonId] changes trigger a new load. The screen owns and releases its [MediaPlayer], while
 * navigation remains callback-based.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailScreen(
    pokemonId: Int,
    onBackClick: () -> Unit,
    showBackButton: Boolean = true,
    viewModel: PokemonDetailViewModel = hiltViewModel<PokemonDetailViewModel>(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = pokemonId) {
        viewModel.setEvent(event = PokemonDetailEvent.LoadPokemon(id = pokemonId))
    }

    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(value = null) }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(viewModel, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiEffect.collect { effect ->
                when (effect) {
                    is PokemonDetailEffect.PlayAudio -> {
                        mediaPlayer?.release()
                        val player = MediaPlayer()
                        try {
                            player.apply {
                                setAudioAttributes(
                                    AudioAttributes
                                        .Builder()
                                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                        .setUsage(AudioAttributes.USAGE_MEDIA)
                                        .build(),
                                )
                                setDataSource(context, effect.url.toUri())
                                setOnPreparedListener { it.start() }
                                setOnCompletionListener {
                                    it.release()
                                    if (mediaPlayer == it) mediaPlayer = null
                                }
                                setOnErrorListener { mp, what, extra ->
                                    Toast
                                        .makeText(
                                            context,
                                            "Errore riproduzione (what:$what, extra:$extra)",
                                            Toast.LENGTH_SHORT,
                                        ).show()
                                    mp.release()
                                    if (mediaPlayer == mp) mediaPlayer = null
                                    true
                                }
                                prepareAsync()
                            }
                            mediaPlayer = player
                        } catch (e: Exception) {
                            Toast
                                .makeText(context, "Errore: ${e.message}", Toast.LENGTH_SHORT)
                                .show()
                            player.release()
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = state.pokemon?.name?.replaceFirstChar { it.uppercase() } ?: "")
                },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.cd_back),
                            )
                        }
                    }
                },
                actions = {
                    val isFavorite = state.pokemon?.isFavorite == true
                    IconButton(onClick = { viewModel.setEvent(PokemonDetailEvent.ToggleFavorite) }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription =
                                if (isFavorite) {
                                    stringResource(
                                        id = R.string.cd_remove_favorite,
                                    )
                                } else {
                                    stringResource(id = R.string.cd_add_favorite)
                                },
                            tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(alignment = Alignment.Center))
                }

                state.errorMessage != null -> {
                    Text(
                        text = state.errorMessage!!.asString(),
                        modifier = Modifier.align(alignment = Alignment.Center).padding(paddingValues),
                    )
                }

                state.pokemon != null -> {
                    PokemonDetailContent(
                        pokemon = state.pokemon!!,
                        paddingValues = paddingValues,
                        onPlayCryClick = { viewModel.setEvent(PokemonDetailEvent.PlayCry) },
                    )
                }
            }
        }
    }
}

/**
 * Stateless detail content for a loaded [pokemon].
 *
 * @param paddingValues insets supplied by the parent scaffold.
 * @param onPlayCryClick callback for the cry playback action.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PokemonDetailContent(
    pokemon: Pokemon,
    paddingValues: PaddingValues = PaddingValues(),
    onPlayCryClick: () -> Unit = {},
) {
    var isVisible by remember { mutableStateOf(value = false) }
    val dimensions = LocalDimensions.current
    val animations = LocalAnimations.current

    LaunchedEffect(key1 = Unit) {
        isVisible = true
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState())
                .padding(
                    start = dimensions.paddingLarge,
                    end = dimensions.paddingLarge,
                    top = dimensions.paddingLarge + paddingValues.calculateTopPadding(),
                    bottom = dimensions.paddingLarge + paddingValues.calculateBottomPadding(),
                ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter =
                fadeIn(animationSpec = tween(durationMillis = animations.durationMedium)) +
                    slideInVertically(
                        initialOffsetY = { -animations.slideOffsetStandard },
                    ),
        ) {
            AsyncImage(
                model = pokemon.imageUrl,
                contentDescription = stringResource(id = R.string.cd_pokemon_image, pokemon.name),
                modifier =
                    Modifier
                        .size(size = dimensions.imageSizeDetail)
                        .clip(shape = RoundedCornerShape(size = dimensions.cornerRadiusLarge))
                        .background(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(all = dimensions.paddingMedium),
                placeholder = if (LocalInspectionMode.current) painterResource(id = android.R.drawable.ic_menu_gallery) else null,
                error = if (LocalInspectionMode.current) painterResource(id = android.R.drawable.ic_menu_gallery) else null,
            )
        }

        Spacer(modifier = Modifier.height(height = dimensions.paddingLarge))

        AnimatedVisibility(
            visible = isVisible,
            enter =
                fadeIn(animationSpec = tween(durationMillis = animations.durationSlow)) +
                    slideInVertically(
                        initialOffsetY = { animations.slideOffsetStandard },
                    ),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = pokemon.name.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.width(width = dimensions.paddingSmall))
                    IconButton(onClick = onPlayCryClick) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = stringResource(id = R.string.cd_play_cry),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(height = dimensions.paddingMedium))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(space = dimensions.paddingSmall),
                    verticalArrangement = Arrangement.spacedBy(space = dimensions.paddingSmall),
                ) {
                    pokemon.types.forEach { type ->
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(size = dimensions.cornerRadiusExtraLarge),
                        ) {
                            Text(
                                text = type,
                                modifier =
                                    Modifier.padding(
                                        horizontal = dimensions.paddingMedium,
                                        vertical = dimensions.paddingSmall,
                                    ),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(height = dimensions.paddingExtraLarge))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(size = dimensions.cornerRadiusExtraLarge),
                    elevation = CardDefaults.cardElevation(defaultElevation = dimensions.elevationLarge),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ),
                ) {
                    Column(
                        modifier = Modifier.padding(all = dimensions.paddingLarge),
                    ) {
                        Text(
                            text = stringResource(id = R.string.base_stats),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = dimensions.paddingMedium),
                        )

                        pokemon.stats.forEach { (statName, statValue) ->
                            StatRow(statName = statName, statValue = statValue)
                            Spacer(modifier = Modifier.height(height = dimensions.paddingMedium))
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = dimensions.paddingMedium),
                            thickness = DividerDefaults.Thickness,
                            color = DividerDefaults.color,
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = stringResource(id = R.string.height),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Text(
                                    text =
                                        stringResource(
                                            id = R.string.height_format,
                                            pokemon.heightInMeters,
                                        ),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = stringResource(id = R.string.weight),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Text(
                                    text =
                                        stringResource(
                                            id = R.string.weight_format,
                                            pokemon.weightInKg,
                                        ),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Renders one base statistic normalized against the PokeAPI maximum of 255. */
@Composable
fun StatRow(
    statName: String,
    statValue: Int,
) {
    val formattedName = statName.replaceFirstChar { it.uppercase() }
    val progress = statValue / 255f
    val dimensions = LocalDimensions.current
    val weights = LocalWeights.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = formattedName,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(weight = weights.statNameWeight),
        )
        Text(
            text = statValue.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(width = dimensions.statValueWidth),
            textAlign = TextAlign.End,
        )
        Spacer(modifier = Modifier.width(width = dimensions.paddingSmall))
        LinearProgressIndicator(
            progress = { progress },
            modifier =
                Modifier
                    .weight(weight = weights.statProgressBarWeight)
                    .height(height = dimensions.statProgressBarHeight)
                    .clip(shape = RoundedCornerShape(size = dimensions.cornerRadiusSmall)),
            color = if (progress > 0.5f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

/** Design-time preview of loaded detail content. */
@DevicePreviews
@Composable
fun PokemonDetailScreenPreview() {
    val mockPokemon =
        Pokemon(
            id = 1,
            name = "Bulbasaur",
            imageUrl = "",
            cryUrl = "",
            types = listOf("Grass", "Poison"),
            height = 7,
            weight = 69,
            stats = mapOf("hp" to 45, "attack" to 49, "defense" to 49, "speed" to 45),
        )
    PokedexTheme {
        Surface {
            PokemonDetailContent(pokemon = mockPokemon)
        }
    }
}
