package com.example.pokedex.di

import android.content.Context
import com.example.pokedex.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object ConfigModule {

    @Provides
    @Named("web_client_id")
    fun provideWebClientId(@ApplicationContext context: Context): String {
        @Suppress("DiscouragedApi")
        val resId = context.resources.getIdentifier(
            "default_web_client_id",
            "string",
            context.packageName
        )
        val fromRes = if (resId != 0) context.getString(resId) else ""
        return fromRes.ifBlank { BuildConfig.WEB_CLIENT_ID }
    }
}
