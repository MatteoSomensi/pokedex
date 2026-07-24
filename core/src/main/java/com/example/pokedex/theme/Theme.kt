package com.example.pokedex.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode

private val DarkColorScheme = darkColorScheme(primary = Purple80, secondary = PurpleGrey80, tertiary = Pink80)

private val LightColorScheme =
  lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
  )

@Composable
fun PokedexTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = true,
  content: @Composable () -> Unit,
) {
  val isInspectionMode = LocalInspectionMode.current
  val colorScheme =
    when {
      !isInspectionMode && dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  val configuration = androidx.compose.ui.platform.LocalConfiguration.current
  val dimensions = if (configuration.screenWidthDp < 600) {
      Dimensions()
  } else {
      Dimensions(
          paddingSmall = 12.androidx.compose.ui.unit.dp,
          paddingMedium = 24.androidx.compose.ui.unit.dp,
          paddingLarge = 36.androidx.compose.ui.unit.dp,
          paddingExtraLarge = 48.androidx.compose.ui.unit.dp,
          imageSizeList = 140.androidx.compose.ui.unit.dp,
          imageSizeDetail = 300.androidx.compose.ui.unit.dp
      )
  }

  androidx.compose.runtime.CompositionLocalProvider(
      LocalDimensions provides dimensions
  ) {
      MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
  }
}
