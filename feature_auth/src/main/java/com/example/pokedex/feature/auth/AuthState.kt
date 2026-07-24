package com.example.pokedex.feature.auth

data class AuthState(
    val email: String = "",
    val password: String = "",
    val isLogin: Boolean = true,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: Int? = null
)
