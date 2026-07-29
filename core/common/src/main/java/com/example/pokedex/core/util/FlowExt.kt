package com.example.pokedex.core.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 * Atomically updates the value using the current state as the receiver of [reducer].
 *
 * Example: `uiState.updateState { copy(isLoading = true) }`.
 */
inline fun <T> MutableStateFlow<T>.updateState(reducer: T.() -> T) {
    update { it.reducer() }
}
