package com.example.pokedex.domain.analytics

/**
 * Interface per tracciare eventi di analytics e crash in modo agnostico rispetto all'SDK.
 * Consente al dominio e alla UI di non dipendere direttamente da Firebase, Datadog, ecc.
 */
interface AnalyticsLogger {
    /**
     * Invia un evento custom con opzionali parametri chiave-valore.
     */
    fun logEvent(
        eventName: String,
        params: Map<String, Any> = emptyMap(),
    )

    /**
     * Imposta l'ID dell'utente loggato.
     */
    fun setUserId(userId: String?)

    /**
     * Aggiunge proprietà utente per la segmentazione (es. "premium_user" -> "true").
     */
    fun setUserProperty(
        name: String,
        value: String,
    )

    /**
     * Logga un'eccezione non fatale.
     */
    fun logNonFatalException(exception: Throwable)
}
