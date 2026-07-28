package com.example.pokedex.di

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(
        @dagger.hilt.android.qualifiers.ApplicationContext context: android.content.Context,
    ): com.google.firebase.analytics.FirebaseAnalytics = com.google.firebase.analytics.FirebaseAnalytics.getInstance(context)

    @Provides
    @Singleton
    fun provideFirebaseCrashlytics(): com.google.firebase.crashlytics.FirebaseCrashlytics =
        com.google.firebase.crashlytics.FirebaseCrashlytics.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseRemoteConfig(): com.google.firebase.remoteconfig.FirebaseRemoteConfig =
        com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance()
}
