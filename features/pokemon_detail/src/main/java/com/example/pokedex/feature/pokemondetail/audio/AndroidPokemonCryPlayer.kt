package com.example.pokedex.feature.pokemondetail.audio

import android.media.AudioAttributes
import android.media.MediaPlayer
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.resume

/**
 * [MediaPlayer]-backed implementation whose lifetime is owned by the detail ViewModel.
 *
 * The implementation keeps at most one player and one pending preparation. Completion, failure,
 * replacement, cancellation, and explicit release all free the underlying platform resource.
 */
class AndroidPokemonCryPlayer
    @Inject
    constructor() : PokemonCryPlayer {
        private val lock = Any()
        private var currentPlayer: MediaPlayer? = null
        private var pendingStart: CancellableContinuation<Unit>? = null

        override suspend fun play(url: String): Result<Unit> {
            if (url.isBlank()) {
                return Result.failure(IllegalArgumentException("The cry URL must not be blank."))
            }

            return try {
                awaitPlaybackStart(url = url)
                Result.success(Unit)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: PokemonCryPlaybackException) {
                Result.failure(exception)
            }
        }

        override fun release() {
            val resources = detachCurrent()
            resources.continuation?.cancel()
            resources.player?.releaseSafely()
        }

        private suspend fun awaitPlaybackStart(url: String) {
            suspendCancellableCoroutine { continuation ->
                val player = MediaPlayer()
                replaceCurrent(player = player, continuation = continuation)

                continuation.invokeOnCancellation {
                    releaseIfCurrent(player = player)
                }

                try {
                    player.setAudioAttributes(AUDIO_ATTRIBUTES)
                    player.setDataSource(url)
                    player.setOnPreparedListener { preparedPlayer ->
                        val isCurrent = synchronized(lock) { currentPlayer === preparedPlayer }
                        if (!isCurrent) {
                            return@setOnPreparedListener
                        }
                        try {
                            preparedPlayer.start()
                            resumeIfCurrent(player = preparedPlayer)
                        } catch (exception: IllegalStateException) {
                            failIfCurrent(
                                player = preparedPlayer,
                                exception = PokemonCryPlaybackException(cause = exception),
                            )
                        }
                    }
                    player.setOnCompletionListener { completedPlayer ->
                        releaseIfCurrent(player = completedPlayer)
                    }
                    player.setOnErrorListener { failedPlayer, what, extra ->
                        failIfCurrent(
                            player = failedPlayer,
                            exception = PokemonCryPlaybackException(what = what, extra = extra),
                        )
                        true
                    }
                    player.prepareAsync()
                } catch (exception: IOException) {
                    failIfCurrent(player = player, exception = PokemonCryPlaybackException(cause = exception))
                } catch (exception: IllegalArgumentException) {
                    failIfCurrent(player = player, exception = PokemonCryPlaybackException(cause = exception))
                } catch (exception: IllegalStateException) {
                    failIfCurrent(player = player, exception = PokemonCryPlaybackException(cause = exception))
                } catch (exception: SecurityException) {
                    failIfCurrent(player = player, exception = PokemonCryPlaybackException(cause = exception))
                }
            }
        }

        private fun replaceCurrent(
            player: MediaPlayer,
            continuation: CancellableContinuation<Unit>,
        ) {
            val previous =
                synchronized(lock) {
                    val resources = PlayerResources(currentPlayer, pendingStart)
                    currentPlayer = player
                    pendingStart = continuation
                    resources
                }
            previous.continuation?.cancel()
            previous.player?.releaseSafely()
        }

        private fun resumeIfCurrent(player: MediaPlayer) {
            val continuation =
                synchronized(lock) {
                    if (currentPlayer !== player) return
                    pendingStart.also { pendingStart = null }
                }
            if (continuation?.isActive == true) {
                continuation.resume(Unit)
            }
        }

        private fun failIfCurrent(
            player: MediaPlayer,
            exception: Exception,
        ) {
            val resources = detachIfCurrent(player = player) ?: return
            resources.player?.releaseSafely()
            if (resources.continuation?.isActive == true) {
                resources.continuation.resumeWith(Result.failure(exception))
            }
        }

        private fun releaseIfCurrent(player: MediaPlayer) {
            val resources = detachIfCurrent(player = player) ?: return
            resources.continuation?.cancel()
            resources.player?.releaseSafely()
        }

        private fun detachIfCurrent(player: MediaPlayer): PlayerResources? =
            synchronized(lock) {
                if (currentPlayer !== player) return null
                detachCurrentLocked()
            }

        private fun detachCurrent(): PlayerResources = synchronized(lock) { detachCurrentLocked() }

        private fun detachCurrentLocked(): PlayerResources {
            val resources = PlayerResources(currentPlayer, pendingStart)
            currentPlayer = null
            pendingStart = null
            return resources
        }

        private companion object {
            val AUDIO_ATTRIBUTES: AudioAttributes =
                AudioAttributes
                    .Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
        }
    }

private fun MediaPlayer.releaseSafely() {
    runCatching { reset() }
    runCatching { release() }
}

private data class PlayerResources(
    val player: MediaPlayer?,
    val continuation: CancellableContinuation<Unit>?,
)

/** Describes a failure while preparing or starting Pokémon cry playback. */
class PokemonCryPlaybackException private constructor(
    message: String,
    cause: Throwable? = null,
) : IllegalStateException(message, cause) {
    constructor(
        what: Int,
        extra: Int,
    ) : this(message = "MediaPlayer failed with what=$what and extra=$extra.")

    constructor(cause: Throwable) : this(message = "MediaPlayer could not start playback.", cause = cause)
}
