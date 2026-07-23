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
                uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun signInWithGoogle(context: Context, serverClientId: String) {
        viewModelScope.launch {
            uiState.update { it.copy(isLoading = true, error = null) }
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
                        uiState.update { it.copy(isLoading = false, isSuccess = true) }
                    }.onFailure { e ->
                        uiState.update { it.copy(isLoading = false, error = e.message) }
                    }
                } else {
                    uiState.update { it.copy(isLoading = false, error = "Unexpected credential type") }
                }
            } catch (e: GetCredentialException) {
                uiState.update { it.copy(isLoading = false, error = e.message) }
            } catch (e: Exception) {
                uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
