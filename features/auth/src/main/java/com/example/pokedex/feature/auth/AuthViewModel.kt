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
 * Owns authentication form state and delegates account operations to [AuthRepository].
 *
 * The ViewModel supports email sign-in, account creation, and exchanging a Google ID token.
 * Provider errors are mapped to user-facing [UiText] values instead of being exposed to the UI.
 *
 * @property webClientId OAuth web client ID required by Credential Manager.
 */
@HiltViewModel
class AuthViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
        @Named("web_client_id") val webClientId: String,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(value = AuthState())

        /** Current immutable form and operation state. */
        val uiState: StateFlow<AuthState> = _uiState

        /** Replaces the email field without starting authentication. */
        fun onEmailChange(email: String) {
            _uiState.updateState { copy(email = email) }
        }

        /** Replaces the password field without starting authentication. */
        fun onPasswordChange(password: String) {
            _uiState.updateState { copy(password = password) }
        }

        /** Switches between sign-in and account-creation modes. */
        fun toggleIsLogin() {
            _uiState.updateState { copy(isLogin = !isLogin) }
        }

        /** Submits the current email/password fields according to the active mode. */
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

        /** Updates loading state for an authentication flow hosted by the route. */
        fun setLoading(isLoading: Boolean) {
            _uiState.updateState { copy(isLoading = isLoading, error = null) }
        }

        /** Stops loading and exposes [errorRes] as a localized authentication failure. */
        fun setAuthError(errorRes: Int) {
            _uiState.updateState {
                copy(
                    isLoading = false,
                    error = UiText.StringResource(id = errorRes),
                )
            }
        }

        /** Exchanges [idToken] with the repository and updates success or error state. */
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
