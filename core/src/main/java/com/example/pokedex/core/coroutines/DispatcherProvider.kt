package com.example.pokedex.core.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Injectable access point for the coroutine dispatchers used by the application.
 *
 * Keeping dispatchers behind this contract prevents application logic from depending directly on
 * [Dispatchers] and lets tests replace them with controlled dispatchers.
 */
interface DispatcherProvider {
    /** Dispatcher for operations that must interact with the UI. */
    val main: CoroutineDispatcher

    /** Dispatcher for blocking I/O, networking, and persistence. */
    val io: CoroutineDispatcher

    /** Dispatcher for CPU-intensive work. */
    val default: CoroutineDispatcher

    /** Unconfined dispatcher, exposed mainly for tests and specialized use cases. */
    val unconfined: CoroutineDispatcher
}

/** Production implementation backed by the standard kotlinx.coroutines dispatchers. */
class DefaultDispatcherProvider : DispatcherProvider {
    override val main: CoroutineDispatcher
        get() = Dispatchers.Main
    override val io: CoroutineDispatcher
        get() = Dispatchers.IO
    override val default: CoroutineDispatcher
        get() = Dispatchers.Default
    override val unconfined: CoroutineDispatcher
        get() = Dispatchers.Unconfined
}
