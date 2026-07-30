package com.example.pokedex.data.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.pokedex.domain.result.AppResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DemoAuthRepositoryTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Before
    fun clearSessionBeforeTest() {
        clearSession()
    }

    @After
    fun clearSessionAfterTest() {
        clearSession()
    }

    @Test
    fun authenticatedUserSurvivesRepositoryRecreation() =
        runTest {
            val repository = DemoAuthRepository(context)
            val result = repository.signInWithEmail(TEST_EMAIL, "unused-password")

            check(result is AppResult.Success)

            val restoredRepository = DemoAuthRepository(context)

            assertEquals(TEST_EMAIL, restoredRepository.currentUser.first()?.email)
        }

    @Test
    fun signOutClearsPersistedSession() =
        runTest {
            val repository = DemoAuthRepository(context)
            repository.signInWithEmail(TEST_EMAIL, "unused-password")

            repository.signOut()

            assertNull(DemoAuthRepository(context).currentUser.first())
        }

    private fun clearSession() {
        context
            .getSharedPreferences(DEMO_SESSION_PREFERENCES, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
    }

    private companion object {
        const val DEMO_SESSION_PREFERENCES = "demo_auth_session"
        const val TEST_EMAIL = "architect@example.com"
    }
}
