package com.example.pokedex.core.ui

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

/**
 * Generates light and dark previews for phone, tablet, and foldable configurations.
 *
 * Applying this annotation to sample composables provides quick theme and basic adaptation checks
 * without duplicating [Preview] configurations.
 */
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.FUNCTION,
)
@Preview(name = "Phone - Light", device = "spec:width=411dp,height=891dp", showBackground = true)
@Preview(
    name = "Phone - Dark",
    device = "spec:width=411dp,height=891dp",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
@Preview(
    name = "Tablet - Light",
    device = "spec:width=1280dp,height=800dp,dpi=240",
    showBackground = true,
)
@Preview(
    name = "Tablet - Dark",
    device = "spec:width=1280dp,height=800dp,dpi=240",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
@Preview(name = "Foldable - Light", device = "spec:width=673dp,height=841dp", showBackground = true)
@Preview(
    name = "Foldable - Dark",
    device = "spec:width=673dp,height=841dp",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
annotation class DevicePreviews

/** Nine canonical width/height combinations used for screen-level screenshot coverage. */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION)
@Preview(name = "Compact Short", device = "spec:width=400dp,height=400dp", showBackground = true)
@Preview(name = "Compact Medium", device = "spec:width=400dp,height=500dp", showBackground = true)
@Preview(name = "Compact Tall", device = "spec:width=400dp,height=1000dp", showBackground = true)
@Preview(name = "Medium Short", device = "spec:width=610dp,height=400dp", showBackground = true)
@Preview(name = "Medium Medium", device = "spec:width=610dp,height=500dp", showBackground = true)
@Preview(name = "Medium Tall", device = "spec:width=610dp,height=1000dp", showBackground = true)
@Preview(name = "Expanded Short", device = "spec:width=900dp,height=400dp", showBackground = true)
@Preview(name = "Expanded Medium", device = "spec:width=900dp,height=500dp", showBackground = true)
@Preview(name = "Expanded Tall", device = "spec:width=900dp,height=1000dp", showBackground = true)
annotation class ScreenSizePreviews

/** Theme and large-font variants for accessibility-focused component screenshots. */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION)
@Preview(name = "Phone Light", device = "spec:width=400dp,height=500dp", showBackground = true)
@Preview(
    name = "Phone Dark",
    device = "spec:width=400dp,height=500dp",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
@Preview(
    name = "Phone Font 150%",
    device = "spec:width=400dp,height=500dp",
    fontScale = 1.5f,
    showBackground = true,
)
annotation class AccessibilityPreviews
