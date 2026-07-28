package com.example.pokedex.domain.repository

import com.example.pokedex.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface defining authentication operations.
 * Exposes a reactive stream of the current [AuthUser] and handles actions like sign in, sign up, and sign out.
 */
interface AuthRepository {
    val currentUser: Flow<AuthUser?>

    suspend fun signInWithEmail(
        email: String,
        password: String,
    ): Result<AuthUser>

    suspend fun signUpWithEmail(
        email: String,
        password: String,
    ): Result<AuthUser>

    /**
     * @param idToken The token obtained from Credential Manager (Google Sign-In)
     */
    suspend fun signInWithGoogle(idToken: String): Result<AuthUser>

    suspend fun signOut()
}
