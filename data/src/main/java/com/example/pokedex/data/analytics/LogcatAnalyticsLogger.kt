package com.example.pokedex.data.analytics

import android.util.Log
import com.example.pokedex.domain.analytics.AnalyticsLogger
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementazione di base che stampa gli analytics nel Logcat.
 * In produzione, puoi creare una FirebaseAnalyticsLogger che implementa la stessa interfaccia.
 */
@Singleton
class LogcatAnalyticsLogger @Inject constructor() : AnalyticsLogger {
    
    private val tag = "AnalyticsLogger"

    override fun logEvent(eventName: String, params: Map<String, Any>) {
        Log.d(tag, "Event: $eventName | Params: $params")
    }

    override fun setUserId(userId: String?) {
        Log.d(tag, "Set UserID: $userId")
    }

    override fun setUserProperty(name: String, value: String) {
        Log.d(tag, "Set Property: $name = $value")
    }

    override fun logNonFatalException(exception: Throwable) {
        Log.e(tag, "Non-fatal exception logged", exception)
    }
}
