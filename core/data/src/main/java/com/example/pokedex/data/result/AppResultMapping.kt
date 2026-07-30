package com.example.pokedex.data.result

import android.database.sqlite.SQLiteException
import com.example.pokedex.domain.result.AppError
import com.example.pokedex.domain.result.AppResult
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException

/** Executes data work while preserving cancellation and mapping provider failures to domain errors. */
@Suppress("TooGenericExceptionCaught")
inline fun <T> appResultOf(block: () -> T): AppResult<T> =
    try {
        AppResult.success(block())
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: Exception) {
        AppResult.failure(exception.toAppError(), exception)
    }

fun Throwable.toAppError(): AppError =
    when (this) {
        is IOException -> AppError.Network
        is SQLiteException -> AppError.Storage
        is FirebaseAuthException -> AppError.Unauthorized
        is HttpException ->
            if (code() == HTTP_UNAUTHORIZED || code() == HTTP_FORBIDDEN) {
                AppError.Unauthorized
            } else {
                AppError.Network
            }
        is IllegalArgumentException -> AppError.InvalidInput
        else -> AppError.Unexpected
    }

private const val HTTP_UNAUTHORIZED = 401
private const val HTTP_FORBIDDEN = 403
