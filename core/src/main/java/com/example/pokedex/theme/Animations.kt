package com.example.pokedex.theme

import androidx.compose.runtime.staticCompositionLocalOf

data class Animations(
    val durationMedium: Int = 500,
    val durationSlow: Int = 700,
    val slideOffsetStandard: Int = 50
)

val LocalAnimations = staticCompositionLocalOf { Animations() }
