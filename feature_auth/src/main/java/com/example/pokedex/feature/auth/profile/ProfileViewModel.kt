package com.example.pokedex.feature.auth.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.domain.model.AuthUser
import com.example.pokedex.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel managing the state and logic for the User Profile screen.
 * Exposes the currently authenticated user's data and handles user session actions
 * such as logging out via [AuthRepository].
 */
@HiltViewModel
class ProfileViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) : ViewModel() {
        val currentUser: StateFlow<AuthUser?> =
            authRepository.currentUser
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(stopTimeoutMillis = STOP_TIMEOUT_MILLIS),
                    initialValue = null,
                )

        fun logout() {
            viewModelScope.launch {
                authRepository.signOut()
            }
        }

        companion object {
            private const val STOP_TIMEOUT_MILLIS = 5000L
        }
    }
