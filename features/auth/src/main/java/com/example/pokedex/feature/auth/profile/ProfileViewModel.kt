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
 * Exposes the current authenticated user and delegates logout to [AuthRepository].
 *
 * The repository flow remains active for five seconds after the last subscriber to avoid
 * unnecessary listener churn during short configuration changes.
 */
@HiltViewModel
class ProfileViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) : ViewModel() {
        /** Current authenticated user, or `null` when the session is absent or not emitted yet. */
        val currentUser: StateFlow<AuthUser?> =
            authRepository.currentUser
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(stopTimeoutMillis = STOP_TIMEOUT_MILLIS),
                    initialValue = null,
                )

        /** Ends the current authentication session asynchronously. */
        fun logout() {
            viewModelScope.launch {
                authRepository.signOut()
            }
        }

        companion object {
            private const val STOP_TIMEOUT_MILLIS = 5000L
        }
    }
