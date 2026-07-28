package com.example.pokedex.data.remoteconfig

import android.util.Log
import com.example.pokedex.domain.remoteconfig.FeatureFlagManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockFeatureFlagManager @Inject constructor() : FeatureFlagManager {

    // Simula i flag con valori di default. In futuro sarà sostituito da Firebase Remote Config.
    private val flags = MutableStateFlow(
        mapOf(
            "enable_new_ui" to false,
            "show_ads" to false
        )
    )

    override suspend fun fetchAndActivate() {
        Log.d("MockFeatureFlag", "Fetching flags from remote...")
        // In una vera implementazione, qui si chiama FirebaseRemoteConfig.fetchAndActivate()
    }

    override fun isFeatureEnabled(flagKey: String): Flow<Boolean> {
        return flags.map { it[flagKey] ?: false }
    }

    override fun isFeatureEnabledSync(flagKey: String): Boolean {
        return flags.value[flagKey] ?: false
    }
}
