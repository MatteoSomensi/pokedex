package com.example.pokedex.core.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<S : UiState, E : UiEvent, F : UiEffect> : ViewModel() {

    private val initialState: S by lazy { createInitialState() }

    val uiState: StateFlow<S> field = MutableStateFlow(initialState)

    private val _uiEvent: MutableSharedFlow<E> = MutableSharedFlow()

    private val _uiEffect: Channel<F> = Channel()
    val uiEffect: Flow<F> = _uiEffect.receiveAsFlow()

    init {
        subscribeToEvents()
    }

    abstract fun createInitialState(): S

    abstract fun handleEvent(event: E)

    protected fun setState(reduce: S.() -> S) {
        val newState = uiState.value.reduce()
        uiState.value = newState
    }

    protected fun setEffect(builder: () -> F) {
        val effectValue = builder()
        viewModelScope.launch { _uiEffect.send(effectValue) }
    }

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
