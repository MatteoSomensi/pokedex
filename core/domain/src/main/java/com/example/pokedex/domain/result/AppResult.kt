package com.example.pokedex.domain.result

/** Failure categories that presentation code can handle without knowing an SDK exception type. */
sealed interface AppError {
    data object Network : AppError

    data object Unauthorized : AppError

    data object Storage : AppError

    data object InvalidInput : AppError

    data object Unexpected : AppError
}

/** Typed application result that keeps provider exceptions behind a stable domain error. */
sealed interface AppResult<out T> {
    data class Success<T>(
        val value: T,
    ) : AppResult<T>

    data class Failure(
        val error: AppError,
        val cause: Throwable? = null,
    ) : AppResult<Nothing>

    val isSuccess: Boolean
        get() = this is Success

    val isFailure: Boolean
        get() = this is Failure

    fun getOrNull(): T? = (this as? Success)?.value

    fun exceptionOrNull(): Throwable? = (this as? Failure)?.cause

    fun onSuccess(block: (T) -> Unit): AppResult<T> {
        if (this is Success) block(value)
        return this
    }

    fun onFailure(block: (AppError) -> Unit): AppResult<T> {
        if (this is Failure) block(error)
        return this
    }

    companion object {
        fun <T> success(value: T): AppResult<T> = Success(value)

        fun failure(
            error: AppError,
            cause: Throwable? = null,
        ): AppResult<Nothing> = Failure(error, cause)
    }
}
