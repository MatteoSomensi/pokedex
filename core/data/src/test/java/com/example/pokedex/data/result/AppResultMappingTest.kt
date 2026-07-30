package com.example.pokedex.data.result

import com.example.pokedex.domain.result.AppError
import com.example.pokedex.domain.result.AppResult
import kotlinx.coroutines.CancellationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Test
import java.io.IOException

class AppResultMappingTest {
    @Test
    fun providerFailuresMapToStableDomainErrors() {
        assertEquals(AppError.Network, IOException("offline").toAppError())
        assertEquals(AppError.InvalidInput, IllegalArgumentException("invalid").toAppError())
        assertEquals(AppError.Unexpected, IllegalStateException("unexpected").toAppError())
    }

    @Test
    fun failureRetainsDiagnosticCause() {
        val cause = IOException("offline")

        val result = appResultOf<Unit> { throw cause }

        check(result is AppResult.Failure)
        assertEquals(AppError.Network, result.error)
        assertSame(cause, result.cause)
    }

    @Test
    fun cancellationIsNeverWrappedAsFailure() {
        val cancellation = CancellationException("cancelled")

        val thrown =
            assertThrows(CancellationException::class.java) {
                appResultOf<Unit> { throw cancellation }
            }

        assertSame(cancellation, thrown)
    }
}
