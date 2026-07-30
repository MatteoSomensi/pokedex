package com.example.pokedex.data.repository

import com.example.pokedex.domain.model.AuthUser
import com.example.pokedex.domain.repository.AuthRepository
import com.example.pokedex.domain.result.AppError
import com.example.pokedex.domain.result.AppResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Deterministic authentication adapter used when Firebase configuration is absent.
 *
 * It lets a fresh clone exercise authenticated navigation without external credentials or network
 * state while preserving the same domain contract used by the Firebase implementation.
 */
@Singleton
class DemoAuthRepository
    @Inject
    constructor() : AuthRepository {
        private val user = MutableStateFlow<AuthUser?>(null)

        override val currentUser: Flow<AuthUser?> = user

        override suspend fun signInWithEmail(
            email: String,
            password: String,
        ): AppResult<AuthUser> = authenticate(email)

        override suspend fun signUpWithEmail(
            email: String,
            password: String,
        ): AppResult<AuthUser> = authenticate(email)

        override suspend fun signInWithGoogle(idToken: String): AppResult<AuthUser> = authenticate(DEMO_EMAIL)

        override suspend fun signOut() {
            user.value = null
        }

        private fun authenticate(email: String): AppResult<AuthUser> {
            if (email.isBlank()) {
                return AppResult.failure(
                    AppError.InvalidInput,
                    IllegalArgumentException("Email is required"),
                )
            }
            val authenticatedUser =
                AuthUser(
                    uid = DEMO_USER_ID,
                    email = email,
                    displayName = DEMO_DISPLAY_NAME,
                )
            user.value = authenticatedUser
            return AppResult.success(authenticatedUser)
        }

        private companion object {
            const val DEMO_USER_ID = "demo-user"
            const val DEMO_EMAIL = "architect@example.com"
            const val DEMO_DISPLAY_NAME = "Demo Architect"
        }
    }
