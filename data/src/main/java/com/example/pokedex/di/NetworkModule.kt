package com.example.pokedex.di

import com.example.pokedex.core.util.Constants
import com.example.pokedex.data.remote.PokeApiService
import com.example.pokedex.data.remote.auth.AuthInterceptor
import com.example.pokedex.data.remote.auth.SecureSessionManager
import com.example.pokedex.data.remote.auth.SessionManager
import com.example.pokedex.data.remote.auth.TokenAuthenticator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

/**
 * This object is responsible for NetworkModule logic.
 * Part of the Clean Architecture structure.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideJson(): Json =
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }

    @Provides
    @Singleton
    fun provideSessionManager(
        @dagger.hilt.android.qualifiers.ApplicationContext context: android.content.Context,
    ): SessionManager = SecureSessionManager(context)

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator,
    ): OkHttpClient {
        val builder =
            OkHttpClient
                .Builder()
                .addInterceptor(authInterceptor)
                .authenticator(tokenAuthenticator)

        if (com.example.pokedex.data.BuildConfig.DEBUG) {
            val logging =
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                }
            builder.addInterceptor(logging)
        }

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        json: Json,
    ): Retrofit {
        val contentType = "application/json; charset=UTF8".toMediaType()
        return Retrofit
            .Builder()
            .baseUrl(Constants.POKE_API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun providePokeApiService(retrofit: Retrofit): PokeApiService = retrofit.create(PokeApiService::class.java)
}
