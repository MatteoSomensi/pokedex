package com.example.pokedex.domain.repository

import com.example.pokedex.domain.model.AuthUser
import com.example.pokedex.domain.result.AppResult
import kotlinx.coroutines.flow.Flow

/**
 * Provider-independent authentication boundary.
 *
 * Operations return [AppResult] so provider failures remain explicit without leaking Firebase
 * types.
 * [currentUser] emits `null` whenever there is no active session.
 */
interface AuthRepository {
    /** Reactive representation of the current authenticated user. */
    val currentUser: Flow<AuthUser?>

    /** Authenticates an existing account with an email and password. */
    suspend fun signInWithEmail(
        email: String,
        password: String,
    ): AppResult<AuthUser>

    /** Creates and authenticates an account with an email and password. */
    suspend fun signUpWithEmail(
        email: String,
        password: String,
    ): AppResult<AuthUser>

    /**
     * Exchanges a Google ID token for an authenticated application user.
     *
     * @param idToken token obtained from Credential Manager Google Sign-In.
     */
    suspend fun signInWithGoogle(idToken: String): AppResult<AuthUser>

    /** Ends the current provider session. */
    suspend fun signOut()
}
