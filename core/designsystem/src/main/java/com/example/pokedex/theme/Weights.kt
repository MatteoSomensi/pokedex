package com.example.pokedex.theme

import androidx.compose.runtime.staticCompositionLocalOf

data class Weights(
    val statNameWeight: Float = 0.3f,
    val statProgressBarWeight: Float = 0.7f,
    val listContentWeight: Float = 1f
)

val LocalWeights = staticCompositionLocalOf { Weights() }
