package com.example.pokedex.domain.analytics

/**
 * SDK-agnostic contract for analytics events and non-fatal error reporting.
 *
 * Domain and presentation code can depend on this interface without importing Firebase or another
 * observability provider.
 */
interface AnalyticsLogger {
    /** Records a custom [eventName] with optional key-value [params]. */
    fun logEvent(
        eventName: String,
        params: Map<String, Any> = emptyMap(),
    )

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
