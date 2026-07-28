package com.example.pokedex.macrobenchmark

import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Measures cold-start latency and frame timing while scrolling the main Pokémon list.
 *
 * Macrobenchmarks run out of process against the target app, which makes the measurements more
 * representative than timings collected from regular unit or UI tests.
 */
@RunWith(AndroidJUnit4::class)
class ExampleStartupBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    /** Repeats the cold-start and scroll scenario while collecting startup and frame metrics. */
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
