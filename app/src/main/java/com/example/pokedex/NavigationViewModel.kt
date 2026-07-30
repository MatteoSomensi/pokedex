package com.example.pokedex

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.domain.model.AuthUser
import com.example.pokedex.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** Exposes provider-independent session state to root navigation. */
@HiltViewModel
class NavigationViewModel
    @Inject
    constructor(
        authRepository: AuthRepository,
    ) : ViewModel() {
        val currentUser: StateFlow<AuthUser?> =
            authRepository.currentUser.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = STATE_STOP_TIMEOUT_MILLIS),
                initialValue = null,
            )
    }

private const val STATE_STOP_TIMEOUT_MILLIS = 5_000L
