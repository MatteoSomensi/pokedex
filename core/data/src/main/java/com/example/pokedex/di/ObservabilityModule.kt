package com.example.pokedex.di

import com.example.pokedex.data.analytics.FirebaseAnalyticsLogger
import com.example.pokedex.data.remoteconfig.FirebaseRemoteConfigManager
import com.example.pokedex.domain.analytics.AnalyticsLogger
import com.example.pokedex.domain.remoteconfig.FeatureFlagManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Binds Firebase-backed observability services to SDK-independent domain contracts. */
@Module
@InstallIn(SingletonComponent::class)
abstract class ObservabilityModule {
    @Binds
    @Singleton
    abstract fun bindAnalyticsLogger(impl: FirebaseAnalyticsLogger): AnalyticsLogger

    @Binds
    @Singleton
    abstract fun bindFeatureFlagManager(impl: FirebaseRemoteConfigManager): FeatureFlagManager
}
