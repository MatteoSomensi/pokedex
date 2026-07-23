package com.example.pokedex.domain.repository

import com.example.pokedex.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<AuthUser?>

    suspend fun signInWithEmail(email: String, password: String): Result<AuthUser>
    suspend fun signUpWithEmail(email: String, password: String): Result<AuthUser>
    
    /**
     * @param idToken The token obtained from Credential Manager (Google Sign-In)
     */
    suspend fun signInWithGoogle(idToken: String): Result<AuthUser>
    
    suspend fun signOut()
}
