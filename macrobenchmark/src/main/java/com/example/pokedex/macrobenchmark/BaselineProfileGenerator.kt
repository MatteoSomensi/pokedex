package com.example.pokedex.macrobenchmark

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Generates a baseline profile from the critical startup-and-scroll user journey.
 *
 * Run this instrumentation test on a supported physical device or emulator through the
 * `:macrobenchmark` Gradle tasks, then package the generated profile with the app.
 */
@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    /** Records startup and list-scrolling code paths for ahead-of-time compilation. */
    @Suppress("MagicNumber")
    @Test
    fun generate() {
        baselineProfileRule.collect(
            packageName = "com.example.pokedex",
            maxIterations = 5,
        ) {
            // This block defines the app's critical user journey. Here we are interested in
            // optimizing for app startup and list scrolling.
            pressHome()
            startActivityAndWait()

            // Wait for the Pokemon list to be displayed
            val pokemonList =
                device.findObject(
                    androidx.test.uiautomator.By
                        .res("pokemon_list"),
                )
            if (pokemonList != null) {
                pokemonList.setGestureMargin(device.displayWidth / 5)
                pokemonList.scroll(androidx.test.uiautomator.Direction.DOWN, 1f)
                device.waitForIdle()
            }
        }
    }
}
