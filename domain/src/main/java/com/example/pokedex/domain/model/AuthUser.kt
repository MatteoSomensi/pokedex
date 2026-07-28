package com.example.pokedex.domain.model

/**
 * Provider-independent representation of an authenticated user.
 *
 * @property uid stable identifier assigned by the authentication provider.
 * @property email nullable account email.
 * @property displayName nullable user-facing name.
 */
data class AuthUser(
    val uid: String,
    val email: String?,
    val displayName: String?,
)
