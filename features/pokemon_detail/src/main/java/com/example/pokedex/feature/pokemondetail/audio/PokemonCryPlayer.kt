package com.example.pokedex.feature.pokemondetail.audio

/**
 * Plays Pokémon cries without exposing Android media APIs to presentation code.
 *
 * [play] completes when playback has started or returns a failure if the source cannot be prepared.
 * A new request replaces any active playback. [release] is idempotent and cancels pending work.
 */
interface PokemonCryPlayer {
    suspend fun play(url: String): Result<Unit>

    fun release()
}
