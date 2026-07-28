package com.example.pokedex.core.designsystem

import androidx.compose.runtime.compositionLocalOf

/**
 * This `CompositionLocal` can be used by a detail `NavEntry` to decide whether to display
 * a back button. Default is `true`. It is set to `false` for a detail `NavEntry` when being
 * displayed in a `ListDetailScene`.
 */
val LocalBackButtonVisibility = compositionLocalOf { true }
