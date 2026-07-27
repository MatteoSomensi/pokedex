package com.example.pokedex.data.coroutines

import com.example.pokedex.core.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher

class TestDispatcherProvider(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : DispatcherProvider {
    override val main: CoroutineDispatcher get() = testDispatcher
    override val io: CoroutineDispatcher get() = testDispatcher
    override val default: CoroutineDispatcher get() = testDispatcher
    override val unconfined: CoroutineDispatcher get() = testDispatcher
}
