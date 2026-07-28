package com.example.pokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.pokedex.theme.PokedexTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * The main entry point of the application.
 * Sets up the edge-to-edge display, applies the [PokedexTheme], and
 * initializes the Compose navigation graph via [MainNavigation].
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val deepLinkUri = androidx.compose.runtime.mutableStateOf<android.net.Uri?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        deepLinkUri.value = intent?.data

        enableEdgeToEdge()
        setContent {
            PokedexTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) { MainNavigation(deepLinkUri = deepLinkUri.value) }
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        deepLinkUri.value = intent.data
    }
}
