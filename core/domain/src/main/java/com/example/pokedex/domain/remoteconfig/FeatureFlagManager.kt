package com.example.pokedex.domain.remoteconfig

import kotlinx.coroutines.flow.Flow

/** Provider-independent contract for remotely controlled Boolean feature flags. */
interface FeatureFlagManager {
    /** Fetches remote values, activates them, and refreshes observable local state. */
    suspend fun fetchAndActivate()

    /** Observes [flag], falling back to its safe local default. */
    fun isFeatureEnabled(flag: FeatureFlag): Flow<Boolean>

    /** Returns the current local value for [flag], or its safe default when unavailable. */
    fun isFeatureEnabledSync(flag: FeatureFlag): Boolean
}

/** Known remotely configurable capabilities and their safe local defaults. */
enum class FeatureFlag(
    val key: String,
    val defaultValue: Boolean,
) {
    NEW_UI("enable_new_ui", false),
    ADS("show_ads", false),
}
