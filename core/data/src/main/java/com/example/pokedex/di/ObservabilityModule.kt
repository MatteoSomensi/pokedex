package com.example.pokedex.di

import com.example.pokedex.data.analytics.FirebaseAnalyticsLogger
import com.example.pokedex.data.analytics.NoOpAnalyticsLogger
import com.example.pokedex.data.remoteconfig.FirebaseRemoteConfigManager
import com.example.pokedex.data.remoteconfig.LocalFeatureFlagManager
import com.example.pokedex.domain.analytics.AnalyticsLogger
import com.example.pokedex.domain.remoteconfig.FeatureFlagManager
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

/** Binds Firebase-backed observability services to SDK-independent domain contracts. */
@Module
@InstallIn(SingletonComponent::class)
object ObservabilityModule {
    @Provides
    @Singleton
    fun provideAnalyticsLogger(
        @Named("firebase_configured") firebaseConfigured: Boolean,
        firebase: Lazy<FirebaseAnalyticsLogger>,
        noOp: NoOpAnalyticsLogger,
    ): AnalyticsLogger = if (firebaseConfigured) firebase.get() else noOp

    @Provides
    @Singleton
    fun provideFeatureFlagManager(
        @Named("firebase_configured") firebaseConfigured: Boolean,
        firebase: Lazy<FirebaseRemoteConfigManager>,
        local: LocalFeatureFlagManager,
    ): FeatureFlagManager = if (firebaseConfigured) firebase.get() else local
}
