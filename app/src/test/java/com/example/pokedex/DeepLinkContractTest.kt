package com.example.pokedex

import android.net.Uri
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DeepLinkContractTest {
    @Test
    fun validPokemonDeepLinkReturnsPositiveId() {
        assertEquals(25, Uri.parse("pokedex://pokemon/25").toPokemonId())
    }

    @Test
    fun malformedOrUntrustedDeepLinksAreRejected() {
        assertNull(Uri.parse("https://pokemon/25").toPokemonId())
        assertNull(Uri.parse("pokedex://other/25").toPokemonId())
        assertNull(Uri.parse("pokedex://pokemon/0").toPokemonId())
        assertNull(Uri.parse("pokedex://pokemon/not-a-number").toPokemonId())
    }
}
