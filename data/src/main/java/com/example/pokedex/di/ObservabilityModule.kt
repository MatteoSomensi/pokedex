package com.example.pokedex.di

import com.example.pokedex.data.analytics.LogcatAnalyticsLogger
import com.example.pokedex.data.remoteconfig.MockFeatureFlagManager
import com.example.pokedex.domain.analytics.AnalyticsLogger
import com.example.pokedex.domain.remoteconfig.FeatureFlagManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ObservabilityModule {
    @Binds
    @Singleton
    abstract fun bindAnalyticsLogger(impl: LogcatAnalyticsLogger): AnalyticsLogger

    @Binds
    @Singleton
    abstract fun bindFeatureFlagManager(impl: MockFeatureFlagManager): FeatureFlagManager
}
