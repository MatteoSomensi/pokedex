package com.example.pokedex.domain.remoteconfig

import kotlinx.coroutines.flow.Flow

/** Provider-independent contract for remotely controlled Boolean feature flags. */
interface FeatureFlagManager {
    /** Fetches remote values, activates them, and refreshes observable local state. */
    suspend fun fetchAndActivate()

    /** Observes [flagKey], emitting `false` when the key is unknown. */
    fun isFeatureEnabled(flagKey: String): Flow<Boolean>

    /** Returns the current local value for [flagKey], or `false` when it is unknown. */
    fun isFeatureEnabledSync(flagKey: String): Boolean
}
