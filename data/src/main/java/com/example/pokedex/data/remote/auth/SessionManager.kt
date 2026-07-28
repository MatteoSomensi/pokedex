package com.example.pokedex.data.remote.auth

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
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
 * A secure implementation of SessionManager using EncryptedSharedPreferences.
 */
@Singleton
class SecureSessionManager
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : SessionManager {
        private val masterKey =
            MasterKey
                .Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

        private val sharedPreferences =
            EncryptedSharedPreferences.create(
                context,
                "secure_session_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
            )

        override fun getAccessToken(): String? = sharedPreferences.getString("access_token", null)

        override fun refreshToken(): String? {
            // No token endpoint is configured for the public PokeAPI client.
            // Returning null prevents retrying a 401 with a fabricated credential.
            return null
        }

        override fun clearSession() {
            sharedPreferences.edit { remove("access_token") }
        }
    }
