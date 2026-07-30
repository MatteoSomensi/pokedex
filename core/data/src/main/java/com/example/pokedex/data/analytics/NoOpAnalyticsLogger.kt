package com.example.pokedex.data.analytics

import com.example.pokedex.domain.analytics.AnalyticsEvent
import com.example.pokedex.domain.analytics.AnalyticsLogger
import javax.inject.Inject
import javax.inject.Singleton

/** Privacy-safe telemetry adapter for demo builds and deterministic tests. */
@Singleton
class NoOpAnalyticsLogger
    @Inject
    constructor() : AnalyticsLogger {
        override fun logEvent(event: AnalyticsEvent) = Unit

        override fun setUserId(userId: String?) = Unit

        override fun setUserProperty(
            name: String,
            value: String,
        ) = Unit

        override fun logNonFatalException(exception: Throwable) = Unit
    }
