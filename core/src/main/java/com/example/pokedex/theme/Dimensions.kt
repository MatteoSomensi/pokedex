package com.example.pokedex.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


/**
 * This class is responsible for Dimensions logic.
 * Part of the Clean Architecture structure.
 */
data class Dimensions(
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
    val imageSizeList: Dp = 100.dp,
    val imageSizeDetail: Dp = 200.dp,
    val statProgressBarHeight: Dp = 8.dp,
    val statValueWidth: Dp = 36.dp
)

val LocalDimensions = staticCompositionLocalOf { Dimensions() }
