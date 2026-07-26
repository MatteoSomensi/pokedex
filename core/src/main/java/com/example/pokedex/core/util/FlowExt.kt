package com.example.pokedex.core.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 * Updates the state using the receiver (this), avoiding the need to use "it"
 * e.g. uiState.updateState { copy(isLoading = true) }
 */
inline fun <T> MutableStateFlow<T>.updateState(reducer: T.() -> T) {
    update { it.reducer() }
}
