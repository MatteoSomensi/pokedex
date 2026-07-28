package com.example.pokedex.feature.auth

import com.example.pokedex.core.util.UiText

/**
 * Immutable state for login and registration.
 *
 * @property isLogin `true` for sign-in mode and `false` for account creation.
 * @property isSuccess terminal success signal consumed by [AuthRoute].
 * @property error localized or dynamic error presented by the screen.
 */
data class AuthState(
    val email: String = "",
    val password: String = "",
    val isLogin: Boolean = true,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: UiText? = null,
)
