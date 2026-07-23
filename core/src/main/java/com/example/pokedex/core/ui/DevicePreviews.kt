package com.example.pokedex.core.ui

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.FUNCTION
)
@Preview(name = "Phone - Light", device = "spec:width=411dp,height=891dp", showBackground = true)
@Preview(name = "Phone - Dark", device = "spec:width=411dp,height=891dp", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(name = "Tablet - Light", device = "spec:width=1280dp,height=800dp,dpi=240", showBackground = true)
@Preview(name = "Tablet - Dark", device = "spec:width=1280dp,height=800dp,dpi=240", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(name = "Foldable - Light", device = "spec:width=673dp,height=841dp", showBackground = true)
@Preview(name = "Foldable - Dark", device = "spec:width=673dp,height=841dp", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
annotation class DevicePreviews
