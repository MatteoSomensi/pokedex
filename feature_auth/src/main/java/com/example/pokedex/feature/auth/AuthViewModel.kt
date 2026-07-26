package com.example.pokedex.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.core.R
import com.example.pokedex.core.util.UiText
import com.example.pokedex.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.pokedex.core.util.updateState
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val uiState: StateFlow<AuthState>
        field = MutableStateFlow(value = AuthState())

    fun onEmailChange(email: String) {
        uiState.updateState { copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        uiState.updateState { copy(password = password) }
    }

    fun toggleIsLogin() {
        uiState.updateState { copy(isLogin = !isLogin) }
    }

    fun submitEmailAuth() {
        viewModelScope.launch {
            uiState.updateState { copy(isLoading = true, error = null) }
            val state = uiState.value
            val result = if (state.isLogin) {
                authRepository.signInWithEmail(state.email, state.password)
            } else {
                authRepository.signUpWithEmail(state.email, state.password)
            }

            result.onSuccess {
                uiState.updateState { copy(isLoading = false, isSuccess = true) }
            }.onFailure { e ->
                uiState.updateState {
                    copy(
                        isLoading = false,
                        error = UiText.StringResource(R.string.error_auth_failed)
                    )
                }
            }
        }
    }

    fun setLoading(isLoading: Boolean) {
        uiState.updateState { copy(isLoading = isLoading, error = null) }
    }

    fun setAuthError(errorRes: Int) {
        uiState.updateState {
            copy(
                isLoading = false,
                error = UiText.StringResource(errorRes)
            )
        }
    }

    fun signInWithGoogleToken(idToken: String) {
        viewModelScope.launch {
            uiState.updateState { copy(isLoading = true, error = null) }
            val authResult = authRepository.signInWithGoogle(idToken)
            authResult.onSuccess {
                uiState.updateState { copy(isLoading = false, isSuccess = true) }
            }.onFailure { e ->
                uiState.updateState {
                    copy(
                        isLoading = false,
                        error = UiText.StringResource(R.string.error_auth_failed)
                    )
                }
            }
        }
    }
}
