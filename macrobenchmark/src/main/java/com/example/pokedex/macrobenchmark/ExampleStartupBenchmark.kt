package com.example.pokedex.macrobenchmark

import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExampleStartupBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Suppress("MagicNumber")
    @Test
    fun startupAndScroll() =
        benchmarkRule.measureRepeated(
            packageName = "com.example.pokedex",
            metrics = listOf(StartupTimingMetric(), androidx.benchmark.macro.FrameTimingMetric()),
            iterations = 5,
            startupMode = StartupMode.COLD,
        ) {
            pressHome()
            startActivityAndWait()

            // Scroll the list to measure frame timing (jank)
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
