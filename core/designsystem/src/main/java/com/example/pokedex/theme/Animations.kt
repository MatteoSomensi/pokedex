package com.example.pokedex.theme

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Motion tokens shared by Pokedex composables.
 *
 * @property durationMedium medium animation duration in milliseconds.
 * @property durationSlow slow animation duration in milliseconds.
 * @property slideOffsetStandard initial slide offset in pixels as consumed by Compose transitions.
 */
data class Animations(
    val durationMedium: Int = 500,
    val durationSlow: Int = 700,
    val slideOffsetStandard: Int = 50,
)

/** Composition-local access to the current [Animations] tokens. */
val LocalAnimations = staticCompositionLocalOf { Animations() }
