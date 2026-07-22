package com.example.pokedex.core.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * Base ViewModel per l'architettura MVI.
 * Gestisce lo stato della UI (State), gli eventi in ingresso (Event) e gli effetti collaterali (Effect).
 */
abstract class BaseViewModel<S : UiState, E : UiEvent, F : UiEffect> : ViewModel() {

    private val initialState: S by lazy { createInitialState() }

    private val _uiState: MutableStateFlow<S> = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    private val _uiEvent: MutableSharedFlow<E> = MutableSharedFlow()

    private val _uiEffect: Channel<F> = Channel()
    val uiEffect = _uiEffect.receiveAsFlow()

    init {
        subscribeToEvents()
    }

    /**
     * Da implementare nelle classi figlie per definire lo stato iniziale.
     */
    abstract fun createInitialState(): S

    /**
     * Da implementare per gestire gli eventi in arrivo dalla UI.
     */
    abstract fun handleEvent(event: E)

    /**
     * Imposta un nuovo stato.
     */
    protected fun setState(reduce: S.() -> S) {
        val newState = _uiState.value.reduce()
        _uiState.value = newState
    }

    /**
     * Invia un effetto alla UI (es. Navigazione).
     */
    protected fun setEffect(builder: () -> F) {
        val effectValue = builder()
        viewModelScope.launch { _uiEffect.send(effectValue) }
    }

    /**
     * Permette alla UI di inviare un evento al ViewModel.
     */
    fun setEvent(event: E) {
        val newEvent = event
        viewModelScope.launch { _uiEvent.emit(newEvent) }
    }

    private fun subscribeToEvents() {
        viewModelScope.launch {
            _uiEvent.collect {
                handleEvent(it)
            }
        }
    }
}
