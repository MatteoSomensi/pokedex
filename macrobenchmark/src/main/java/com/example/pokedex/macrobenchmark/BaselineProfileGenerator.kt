package com.example.pokedex.macrobenchmark

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generate() {
        baselineProfileRule.collect(
            packageName = "com.example.pokedex"
        ) {
            // This block defines the app's critical user journey. Here we are interested in
            // optimizing for app startup.
            pressHome()
            startActivityAndWait()
        }
    }
}
