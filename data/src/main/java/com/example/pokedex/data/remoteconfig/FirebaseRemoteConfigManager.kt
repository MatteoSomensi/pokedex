package com.example.pokedex.data.remoteconfig

import android.util.Log
import com.example.pokedex.domain.remoteconfig.FeatureFlagManager
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await

@Singleton
class FirebaseRemoteConfigManager @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig
) : FeatureFlagManager {

    private val flags = MutableStateFlow<Map<String, Boolean>>(emptyMap())

    init {
        // Set default values before fetching
        val defaults = mapOf(
            "enable_new_ui" to false,
            "show_ads" to false
        )
        remoteConfig.setDefaultsAsync(defaults)
        flags.value = defaults
    }

    override suspend fun fetchAndActivate() {
        try {
            val updated = remoteConfig.fetchAndActivate().await()
            if (updated) {
                Log.d("FirebaseRemoteConfig", "Config updated from remote")
            }
            
            // Read all keys from remote config to update the flow
            val newFlags = mutableMapOf<String, Boolean>()
            remoteConfig.all.keys.forEach { key ->
                newFlags[key] = remoteConfig.getBoolean(key)
            }
            flags.value = newFlags
        } catch (e: Exception) {
            Log.e("FirebaseRemoteConfig", "Failed to fetch remote config", e)
        }
    }

    override fun isFeatureEnabled(flagKey: String): Flow<Boolean> = flags.map { it[flagKey] ?: false }

    override fun isFeatureEnabledSync(flagKey: String): Boolean = remoteConfig.getBoolean(flagKey)
}
