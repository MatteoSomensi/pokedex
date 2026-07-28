package com.example.pokedex.widget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

/** Broadcast receiver that connects Android's app-widget lifecycle to [PokedexWidget]. */
class PokedexWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = PokedexWidget()
}
