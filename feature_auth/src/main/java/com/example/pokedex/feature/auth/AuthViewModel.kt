package com.example.pokedex.feature.auth

import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.domain.repository.AuthRepository
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.content.Context

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthState())
    val uiState: StateFlow<AuthState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
    }
    
    fun toggleIsLogin() {
        _uiState.update { it.copy(isLogin = !it.isLogin) }
    }

    fun submitEmailAuth() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val state = _uiState.value
            val result = if (state.isLogin) {
                authRepository.signInWithEmail(state.email, state.password)
            } else {
                authRepository.signUpWithEmail(state.email, state.password)
            }
            
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun signInWithGoogle(context: Context, serverClientId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val credentialManager = CredentialManager.create(context)
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(serverClientId)
                    .setAutoSelectEnabled(true)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(context, request)
                val credential = result.credential
                
                if (credential is GoogleIdTokenCredential) {
                    val idToken = credential.idToken
                    val authResult = authRepository.signInWithGoogle(idToken)
                    authResult.onSuccess {
                        _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                    }.onFailure { e ->
                        _uiState.update { it.copy(isLoading = false, error = e.message) }
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Unexpected credential type") }
                }
            } catch (e: GetCredentialException) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
