package com.example.pokedex.core.mvi

/** Marker for a complete, immutable snapshot of renderable UI state. */
interface UiState

/**
 * Marker for a user action or intention, such as a click or pull-to-refresh, or for a system event
 * that must be handled by a ViewModel.
 */
interface UiEvent

/**
 * Marker for a one-shot effect that must not be modeled as persistent state.
 *
 * Examples include navigation, a Snackbar, or starting audio playback.
 */
interface UiEffect
