package com.example.pokedex.core.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.res.stringResource

/**
 * Presentable text that is either already resolved or references a localized string resource.
 *
 * Keeping the resource ID in state avoids resolving strings in a ViewModel. The value can be
 * resolved during composition with [asString] or outside Compose with [asString].
 */
@Stable
sealed interface UiText {
    /** Dynamic text that does not require Android resource resolution. */
    data class Dynamic(
        val value: String,
    ) : UiText

    /**
     * Reference to a string resource with optional formatting arguments.
     *
     * @property id the `R.string` resource identifier.
     * @property args arguments in the order required by the resource.
     */
    @Stable
    data class StringResource(
        @StringRes val id: Int,
        val args: List<Any> = emptyList(),
    ) : UiText

    /** Resolves the text using resources from the current composition. */
    @Composable
    fun asString(): String =
        when (this) {
            is Dynamic -> value
            is StringResource -> {
                if (args.isEmpty()) {
                    stringResource(id)
                } else {
                    stringResource(id, *args.toTypedArray())
                }
            }
        }

    /** Resolves the text using the supplied [context]. */
    fun asString(context: Context): String =
        when (this) {
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
