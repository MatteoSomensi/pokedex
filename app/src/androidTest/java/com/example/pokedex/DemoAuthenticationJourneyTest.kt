package com.example.pokedex

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.pokedex.core.R
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Release-candidate journey that proves a credential-free clone can enter the authenticated app.
 */
@RunWith(AndroidJUnit4::class)
class DemoAuthenticationJourneyTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun signInWithDemoAdapterOpensHome() {
        assertFalse(BuildConfig.FIREBASE_CONFIGURED)
        val emailLabel = composeRule.activity.getString(R.string.auth_email)
        val passwordLabel = composeRule.activity.getString(R.string.auth_password)
        val signInLabel = composeRule.activity.getString(R.string.auth_sign_in)
        val homeLabel = composeRule.activity.getString(R.string.navigation_home)

        composeRule.onNodeWithText(emailLabel).performTextInput("architect@example.com")
        composeRule.onNodeWithText(passwordLabel).performTextInput("password")
        composeRule.onNode(hasText(signInLabel) and hasClickAction()).performClick()

        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText(homeLabel).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText(homeLabel).assertIsDisplayed()
    }
}
