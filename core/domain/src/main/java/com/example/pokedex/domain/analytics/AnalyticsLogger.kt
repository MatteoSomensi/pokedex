package com.example.pokedex.domain.analytics

/**
 * SDK-agnostic contract for analytics events and non-fatal error reporting.
 *
 * Domain and presentation code can depend on this interface without importing Firebase or another
 * observability provider.
 */
interface AnalyticsLogger {
    /** Records one centrally named and structured analytics event. */
    fun logEvent(event: AnalyticsEvent)

    /** Associates future telemetry with [userId], or clears the association when it is `null`. */
    fun setUserId(userId: String?)

    /** Sets a named user property for segmentation and diagnostics. */
    fun setUserProperty(
        name: String,
        value: String,
    )

    /** Records [exception] as a handled, non-fatal failure. */
    fun logNonFatalException(exception: Throwable)
}

/** Provider-independent telemetry event. */
data class AnalyticsEvent(
    val name: Name,
    val parameters: Map<String, Any> = emptyMap(),
) {
    /** Stable event names shared by all observability adapters. */
    enum class Name(
        val wireValue: String,
    ) {
        POKEMON_OPENED("pokemon_opened"),
        FAVORITE_CHANGED("favorite_changed"),
        AUTH_COMPLETED("auth_completed"),
        SYNC_COMPLETED("sync_completed"),
    }
}
