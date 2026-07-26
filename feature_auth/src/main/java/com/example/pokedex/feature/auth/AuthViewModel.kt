package com.example.pokedex.feature.auth

import com.example.pokedex.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val uiState: StateFlow<AuthState>
        field = MutableStateFlow(AuthState())

    fun onEmailChange(email: String) {
        uiState.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        uiState.update { it.copy(password = password) }
    }

    fun toggleIsLogin() {
        uiState.update { it.copy(isLogin = !it.isLogin) }
    }

    fun submitEmailAuth() {
        viewModelScope.launch {
            uiState.update { it.copy(isLoading = true, error = null) }
            val state = uiState.value
            val result = if (state.isLogin) {
                authRepository.signInWithEmail(state.email, state.password)
            } else {
                authRepository.signUpWithEmail(state.email, state.password)
            }

            result.onSuccess {
                uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { e ->
                uiState.update {
                    it.copy(
                        isLoading = false,
                        error = UiText.StringResource(R.string.error_auth_failed)
                    )
                }
            }
        }
    }

    fun setLoading(isLoading: Boolean) {
        uiState.update { it.copy(isLoading = isLoading, error = null) }
    }

    fun setAuthError(errorRes: Int) {
        uiState.update {
            it.copy(
                isLoading = false,
                error = UiText.StringResource(errorRes)
            )
        }
    }

    fun signInWithGoogleToken(idToken: String) {
        viewModelScope.launch {
            uiState.update { it.copy(isLoading = true, error = null) }
            val authResult = authRepository.signInWithGoogle(idToken)
            authResult.onSuccess {
                uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { e ->
                uiState.update {
                    it.copy(
                        isLoading = false,
                        error = UiText.StringResource(R.string.error_auth_failed)
                    )
                }
            }
        }
    }
}
