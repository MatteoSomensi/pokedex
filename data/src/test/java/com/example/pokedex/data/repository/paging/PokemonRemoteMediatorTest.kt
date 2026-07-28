package com.example.pokedex.data.repository.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.pokedex.data.local.PokedexDatabase
import com.example.pokedex.data.local.entity.PokemonEntity
import com.example.pokedex.data.remote.PokeApiService
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalPagingApi::class)
class PokemonRemoteMediatorTest {
    @Test
    fun `append with no cached items reports end of pagination`() =
        runTest {
            val mediator =
                PokemonRemoteMediator(
                    api = mockk<PokeApiService>(),
                    db = mockk<PokedexDatabase>(),
                    query = "missing",
                    fetchAllPokemon = { emptyList() },
                )
            val state =
                PagingState<Int, PokemonEntity>(
                    pages = emptyList(),
                    anchorPosition = null,
                    config = PagingConfig(pageSize = 20),
                    leadingPlaceholderCount = 0,
                )

            val result = mediator.load(LoadType.APPEND, state)

            assertTrue(result is RemoteMediator.MediatorResult.Success)
            assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
        }
}
