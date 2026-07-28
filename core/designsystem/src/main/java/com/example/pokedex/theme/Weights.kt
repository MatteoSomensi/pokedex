package com.example.pokedex.theme

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Relative layout weights shared by reusable Pokedex components.
 *
 * @property statNameWeight fraction assigned to a statistic label.
 * @property statProgressBarWeight fraction assigned to its progress indicator.
 * @property listContentWeight fraction assigned to the list content below controls.
 */
data class Weights(
    val statNameWeight: Float = 0.3f,
    val statProgressBarWeight: Float = 0.7f,
    val listContentWeight: Float = 1f,
)

/** Composition-local access to the current [Weights] tokens. */
val LocalWeights = staticCompositionLocalOf { Weights() }
