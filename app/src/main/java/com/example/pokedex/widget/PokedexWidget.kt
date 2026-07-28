package com.example.pokedex.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.text.FontWeight
import androidx.glance.unit.ColorProvider

class PokedexWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            PokedexWidgetContent()
        }
    }
}

@Composable
fun PokedexWidgetContent() {
    Column(
        modifier = GlanceModifier.fillMaxSize()
            .background(ColorProvider(androidx.compose.ui.graphics.Color.White))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Pokedex Widget",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                color = ColorProvider(androidx.compose.ui.graphics.Color.Black)
            )
        )
        Text(
            text = "Catch 'em all!",
            style = TextStyle(
                color = ColorProvider(androidx.compose.ui.graphics.Color.DarkGray)
            ),
            modifier = GlanceModifier.padding(top = 8.dp)
        )
    }
}
