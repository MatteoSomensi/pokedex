package com.example.pokedex.data.remoteconfig

import com.example.pokedex.domain.remoteconfig.FeatureFlag
import com.example.pokedex.domain.remoteconfig.FeatureFlagManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/** Deterministic local feature flags for demo builds. */
@Singleton
class LocalFeatureFlagManager
    @Inject
    constructor() : FeatureFlagManager {
        private val flags = MutableStateFlow(DEFAULT_FLAGS)

        override suspend fun fetchAndActivate() = Unit

        override fun isFeatureEnabled(flag: FeatureFlag): Flow<Boolean> =
            flags.map { values ->
                values[flag.key] ?: flag.defaultValue
            }

        override fun isFeatureEnabledSync(flag: FeatureFlag): Boolean = flags.value[flag.key] ?: flag.defaultValue

        private companion object {
            val DEFAULT_FLAGS = FeatureFlag.entries.associate { flag -> flag.key to flag.defaultValue }
        }
    }
