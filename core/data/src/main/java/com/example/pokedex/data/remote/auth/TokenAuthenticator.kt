package com.example.pokedex.data.remote.auth

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

/**
 * An OkHttp [Authenticator] that triggers when the server returns a 401 Unauthorized status code.
 * It attempts to refresh the session token and retry the failed request.
 */
class TokenAuthenticator
    @Inject
    constructor(
        private val sessionManager: SessionManager,
    ) : Authenticator {
        /**
         * Authenticates the request when a 401 Unauthorized status is received.
         * Prevents infinite loops by checking the response count.
         * Synchronizes token refresh to avoid multiple concurrent refresh requests.
         *
         * @param route The route for the connection.
         * @param response The response containing the 401 status.
         * @return The modified request with the new token, or null if refresh failed or retry limit reached.
         */
        override fun authenticate(
            route: Route?,
            response: Response,
        ): Request? {
            if (response.responseCount >= 2) {
                return null
            }

            val newToken =
                synchronized(this) {
                    val currentToken = sessionManager.getAccessToken()
                    val requestToken = response.request.header("Authorization")?.removePrefix("Bearer ")

                    if (currentToken != null && currentToken != requestToken) {
                        currentToken
                    } else {
                        val refreshed = sessionManager.refreshToken()
                        if (refreshed == null) {
                            sessionManager.clearSession()
                        }
                        refreshed
                    }
                }

            return if (newToken != null) {
                response.request
                    .newBuilder()
                    .header("Authorization", "Bearer $newToken")
                    .build()
            } else {
                null
            }
        }

        /**
         * Helper extension to count how many times this request has been retried.
         */
        private val Response.responseCount: Int
            get() {
                var count = 1
                var priorResponse = priorResponse
                while (priorResponse != null) {
                    count++
                    priorResponse = priorResponse.priorResponse
                }
                return count
            }
    }
