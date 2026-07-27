package com.example.pokedex.data.remote.auth

import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interface for managing session tokens.
 */
interface SessionManager {
    /** Retrieves the current access token, if any. */
    fun getAccessToken(): String?
    
    /** 
     * Attempts to refresh the access token synchronously. 
     * @return The new access token, or null if refresh failed (e.g. refresh token expired).
     */
    fun refreshToken(): String?
    
    /** Logs the user out locally (clears tokens). */
    fun clearSession()
}

/**
 * A dummy implementation for didactic purposes.
 * In a real app, this would use DataStore or EncryptedSharedPreferences.
 */
@Singleton
class DummySessionManager @Inject constructor() : SessionManager {
    
    private var dummyToken: String? = "dummy_initial_token"

    /**
     * Retrieves the dummy token.
     * In a real application, this would read from DataStore synchronously.
     *
     * @return The current access token.
     */
    override fun getAccessToken(): String? {
        return dummyToken
    }

    /**
     * Refreshes the dummy token by generating a new one.
     * In a real application, this would make a synchronous network call to the refresh endpoint.
     *
     * @return The refreshed access token.
     */
    override fun refreshToken(): String? {
        println("Refreshing token...")
        dummyToken = "dummy_refreshed_token_${System.currentTimeMillis()}"
        return dummyToken
    }

    override fun clearSession() {
        dummyToken = null
    }
}
