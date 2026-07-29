package com.example.pokedex.data.remoteconfig

import android.util.Log
import com.example.pokedex.domain.remoteconfig.FeatureFlagManager
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Real implementation of [FeatureFlagManager] that interfaces with Firebase Remote Config.
 *
 * It handles the fetching of feature flags from the remote server, activates them,
 * and exposes them via a reactive [Flow] or synchronously. This allows the app
 * to dynamically adjust its behavior or UI without requiring a new app release.
 *
 * @property remoteConfig The [FirebaseRemoteConfig] instance used to fetch and store config values.
 */
@Singleton
class FirebaseRemoteConfigManager
    @Inject
    constructor(
        private val remoteConfig: FirebaseRemoteConfig,
    ) : FeatureFlagManager {
        private val flags = MutableStateFlow(DEFAULT_FLAGS)

        init {
            remoteConfig.setDefaultsAsync(DEFAULT_FLAGS)
        }

        override suspend fun fetchAndActivate() {
            try {
                val updated = remoteConfig.fetchAndActivate().await()
                if (updated) {
                    Log.d(TAG, "Config updated from remote")
                }

                flags.value =
                    remoteConfig.all.keys.associateWith { key ->
                        remoteConfig.getBoolean(key)
                    }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: FirebaseRemoteConfigException) {
                Log.e(TAG, "Failed to fetch remote config", exception)
            }
        }

        override fun isFeatureEnabled(flagKey: String): Flow<Boolean> = flags.map { it[flagKey] ?: false }

        override fun isFeatureEnabledSync(flagKey: String): Boolean = flags.value[flagKey] ?: false

        private companion object {
            const val TAG = "FirebaseRemoteConfig"

            val DEFAULT_FLAGS =
                mapOf(
                    "enable_new_ui" to false,
                    "show_ads" to false,
                )
        }
    }
