package com.example.pokedex.data.analytics

import android.os.Bundle
import com.example.pokedex.domain.analytics.AnalyticsEvent
import com.example.pokedex.domain.analytics.AnalyticsLogger
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Real implementation of [AnalyticsLogger] that logs events to Firebase Analytics and
 * records non-fatal exceptions and user properties to Firebase Crashlytics.
 *
 * This class serves as a centralized observability wrapper around Firebase SDKs.
 *
 * @property firebaseAnalytics The [FirebaseAnalytics] instance for tracking app usage and behavioral data.
 * @property firebaseCrashlytics The [FirebaseCrashlytics] instance for recording crashes and non-fatal errors.
 */
@Singleton
class FirebaseAnalyticsLogger
    @Inject
    constructor(
        private val firebaseAnalytics: FirebaseAnalytics,
        private val firebaseCrashlytics: FirebaseCrashlytics,
    ) : AnalyticsLogger {
        override fun logEvent(event: AnalyticsEvent) {
            val bundle =
                Bundle().apply {
                    event.parameters.forEach { (key, value) ->
                        when (value) {
                            is String -> putString(key, value)
                            is Byte, is Short, is Int, is Long -> putLong(key, (value as Number).toLong())
                            is Float, is Double -> putDouble(key, (value as Number).toDouble())
                            is Boolean -> putLong(key, if (value) 1L else 0L)
                            else -> putString(key, value.toString())
                        }
                    }
                }
            firebaseAnalytics.logEvent(event.name.wireValue, bundle)
        }

        override fun setUserId(userId: String?) {
            firebaseAnalytics.setUserId(userId)
            firebaseCrashlytics.setUserId(userId.orEmpty())
        }

        override fun setUserProperty(
            name: String,
            value: String,
        ) {
            firebaseAnalytics.setUserProperty(name, value)
            firebaseCrashlytics.setCustomKey(name, value)
        }

        override fun logNonFatalException(exception: Throwable) {
            firebaseCrashlytics.recordException(exception)
        }
    }
