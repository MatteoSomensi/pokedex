package com.example.pokedex.data.remote.auth

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * An OkHttp [Interceptor] that automatically attaches the access token to outgoing requests.
 */
class AuthInterceptor
    @Inject
    constructor(
        private val sessionManager: SessionManager,
    ) : Interceptor {
        /**
         * Intercepts the outgoing request to append the authorization header.
         *
         * @param chain The interceptor chain.
         * @return The response after proceeding with the optionally modified request.
         */
        override fun intercept(chain: Interceptor.Chain): Response {
            val requestBuilder = chain.request().newBuilder()
            val token = sessionManager.getAccessToken()

            if (!token.isNullOrBlank()) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }

            return chain.proceed(requestBuilder.build())
        }
    }
