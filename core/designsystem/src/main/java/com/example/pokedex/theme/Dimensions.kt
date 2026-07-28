package com.example.pokedex.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Immutable size and spacing tokens consumed by the Pokedex design system.
 *
 * [PokedexTheme] provides compact defaults and a larger token set for windows at least 600 dp
 * wide. Components should consume these values through [LocalDimensions] instead of introducing
 * unrelated literals.
 */
data class Dimensions(
    val paddingExtraSmall: Dp = 4.dp,
    val paddingSmall: Dp = 8.dp,
    val paddingMedium: Dp = 16.dp,
    val paddingLarge: Dp = 24.dp,
    val paddingExtraLarge: Dp = 32.dp,
    val elevationDefault: Dp = 4.dp,
    val elevationLarge: Dp = 8.dp,
    val cornerRadiusSmall: Dp = 4.dp,
    val cornerRadiusMedium: Dp = 8.dp,
    val cornerRadiusLarge: Dp = 16.dp,
    val cornerRadiusExtraLarge: Dp = 24.dp,
    val iconSizeMedium: Dp = 24.dp,
    val iconSizeLarge: Dp = 48.dp,
    val imageSizeList: Dp = 100.dp,
    val imageSizeDetail: Dp = 200.dp,
    val statProgressBarHeight: Dp = 8.dp,
    val statValueWidth: Dp = 36.dp,
    val gridCellMinSize: Dp = 150.dp,
    val imageSizeProfile: Dp = 100.dp,
)

/** Composition-local access to the current adaptive [Dimensions]. */
val LocalDimensions = staticCompositionLocalOf { Dimensions() }
