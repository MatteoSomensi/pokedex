package com.example.pokedex.core.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.res.stringResource

@Stable
sealed interface UiText {
    data class Dynamic(val value: String) : UiText

    @Stable
    data class StringResource(
        @StringRes val id: Int,
        val args: List<Any> = emptyList()
    ) : UiText

    @Composable
    fun asString(): String {
        return when (this) {
            is Dynamic -> value
            is StringResource -> {
                if (args.isEmpty()) {
                    stringResource(id)
                } else {
                    stringResource(id, *args.toTypedArray())
                }
            }
        }
    }

    fun asString(context: Context): String {
        return when (this) {
            is Dynamic -> value
            is StringResource -> {
                if (args.isEmpty()) {
                    context.getString(id)
                } else {
                    context.getString(id, *args.toTypedArray())
                }
            }
        }
    }
}
