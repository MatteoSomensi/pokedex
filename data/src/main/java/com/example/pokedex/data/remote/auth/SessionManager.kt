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

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext

/**
 * A secure implementation of SessionManager using EncryptedSharedPreferences.
 */
@Singleton
class SecureSessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) : SessionManager {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_session_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override fun getAccessToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    override fun refreshToken(): String? {
        // In a real application, make a network call to the refresh endpoint.
        // For demonstration, we just generate a new token and save it.
        val newToken = "secure_refreshed_token_${System.currentTimeMillis()}"
        sharedPreferences.edit().putString("access_token", newToken).apply()
        return newToken
    }

    override fun clearSession() {
        sharedPreferences.edit().remove("access_token").apply()
    }

    // Temporary helper to simulate login setting the token
    fun setAccessToken(token: String) {
        sharedPreferences.edit().putString("access_token", token).apply()
    }
}
