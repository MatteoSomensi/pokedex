package com.example.pokedex.core.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Minimal ViewModel implementation for unidirectional data flow.
 *
 * Persistent screen state is exposed through [uiState], while [uiEffect] carries one-shot events.
 * Events sent through [setEvent] are processed in order by [handleEvent] in [viewModelScope].
 *
 * @param S immutable state required to render the UI.
 * @param E events produced by the UI or the system.
 * @param F one-shot effects consumed by the UI.
 */
abstract class BaseViewModel<S : UiState, E : UiEvent, F : UiEffect> : ViewModel() {
    private val initialState: S by lazy { createInitialState() }

    private val _uiState = MutableStateFlow(value = initialState)

    /** Current observable state, initialized lazily by [createInitialState]. */
    val uiState: StateFlow<S> = _uiState

    private val _uiEvent: MutableSharedFlow<E> = MutableSharedFlow()

    private val _uiEffect: Channel<F> = Channel()

    /**
     * One-shot effects delivered to a single collector.
     *
     * Effects are not replayed to later collectors and must not contain state required to recreate
     * the screen.
     */
    val uiEffect: Flow<F> = _uiEffect.receiveAsFlow()

    init {
        subscribeToEvents()
    }

    /** Creates the initial state before the first event is processed. */
    abstract fun createInitialState(): S

    /** Reduces [event] into new state, an effect, or asynchronous work. */
    abstract fun handleEvent(event: E)

    /** Atomically updates state by applying [reduce] to the current value. */
    protected fun setState(reduce: S.() -> S) {
        _uiState.update { it.reduce() }
    }

    /** Sends the effect produced by [builder] through the one-shot channel. */
    protected fun setEffect(builder: () -> F) {
        val effectValue = builder()
        viewModelScope.launch { _uiEffect.send(element = effectValue) }
    }

    /** Enqueues [event] for processing within the ViewModel lifecycle. */
    fun setEvent(event: E) {
        viewModelScope.launch { _uiEvent.emit(value = event) }
    }

    private fun subscribeToEvents() {
        viewModelScope.launch {
            _uiEvent.collect {
                handleEvent(it)
            }
        }
    }
}
