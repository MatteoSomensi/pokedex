package com.example.pokedex.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.core.R
import com.example.pokedex.core.util.UiText
import com.example.pokedex.core.util.updateState
import com.example.pokedex.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

/**
 * ViewModel managing the state and logic for the Authentication screen.
 * Handles user input (email, password), toggling between Login/Register modes,
 * and communicating with [AuthRepository] to perform authentication operations.
 */
@HiltViewModel
class AuthViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
        @Named("web_client_id") val webClientId: String,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(value = AuthState())
        val uiState: StateFlow<AuthState> = _uiState

        fun onEmailChange(email: String) {
            _uiState.updateState { copy(email = email) }
        }

        fun onPasswordChange(password: String) {
            _uiState.updateState { copy(password = password) }
        }

        fun toggleIsLogin() {
            _uiState.updateState { copy(isLogin = !isLogin) }
        }

        fun submitEmailAuth() {
            viewModelScope.launch {
                _uiState.updateState { copy(isLoading = true, error = null) }
                val state = uiState.value
                val result =
                    if (state.isLogin) {
                        authRepository.signInWithEmail(state.email, state.password)
                    } else {
                        authRepository.signUpWithEmail(state.email, state.password)
                    }

                result
                    .onSuccess {
                        _uiState.updateState { copy(isLoading = false, isSuccess = true) }
                    }.onFailure {
                        _uiState.updateState {
                            copy(
                                isLoading = false,
                                error = UiText.StringResource(id = R.string.error_auth_failed),
                            )
                        }
                    }
            }
        }

        fun setLoading(isLoading: Boolean) {
            _uiState.updateState { copy(isLoading = isLoading, error = null) }
        }

        fun setAuthError(errorRes: Int) {
            _uiState.updateState {
                copy(
                    isLoading = false,
                    error = UiText.StringResource(id = errorRes),
                )
            }
        }

        fun signInWithGoogleToken(idToken: String) {
            viewModelScope.launch {
                _uiState.updateState { copy(isLoading = true, error = null) }
                val authResult = authRepository.signInWithGoogle(idToken)
                authResult
                    .onSuccess {
                        _uiState.updateState { copy(isLoading = false, isSuccess = true) }
                    }.onFailure {
                        _uiState.updateState {
                            copy(
                                isLoading = false,
                                error = UiText.StringResource(id = R.string.error_auth_failed),
                            )
                        }
                    }
            }
        }
    }
