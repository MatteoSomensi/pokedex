package com.example.pokedex.core.mvi

/**
 * Rappresenta lo stato della UI in un dato momento.
 * Deve essere immutabile (data class).
 */
interface UiState

/**
 * Rappresenta un'azione o intenzione dell'utente (es. click di un bottone, swipe to refresh)
 * o un evento di sistema che deve essere gestito dal ViewModel.
 */
interface UiEvent

/**
 * Rappresenta un effetto "one-shot" che deve essere consumato una sola volta dalla UI
 * (es. mostrare una Snackbar, navigare verso un'altra schermata).
 */
interface UiEffect
