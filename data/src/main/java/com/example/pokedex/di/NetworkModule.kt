package com.example.pokedex.di

import com.example.pokedex.core.util.Constants
import com.example.pokedex.data.remote.PokeApiService
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
import com.example.pokedex.data.remote.auth.AuthInterceptor
import com.example.pokedex.data.remote.auth.SecureSessionManager
import com.example.pokedex.data.remote.auth.SessionManager
import com.example.pokedex.data.remote.auth.TokenAuthenticator

@Module
@InstallIn(SingletonComponent::class)

/**
 * This object is responsible for NetworkModule logic.
 * Part of the Clean Architecture structure.
 */
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideSessionManager(@dagger.hilt.android.qualifiers.ApplicationContext context: android.content.Context): SessionManager {
        return SecureSessionManager(context)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit {
        val contentType = "application/json; charset=UTF8".toMediaType()
        return Retrofit.Builder()
            .baseUrl(Constants.POKE_API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun providePokeApiService(retrofit: Retrofit): PokeApiService {
        return retrofit.create(PokeApiService::class.java)
    }
}
