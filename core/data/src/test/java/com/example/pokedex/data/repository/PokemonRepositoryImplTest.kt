package com.example.pokedex.data.repository

import com.example.pokedex.data.coroutines.TestDispatcherProvider
import com.example.pokedex.data.local.PokedexDatabase
import com.example.pokedex.data.local.dao.PokemonDao
import com.example.pokedex.data.local.entity.PokemonEntity
import com.example.pokedex.data.remote.PokeApiService
import com.example.pokedex.data.remote.model.PokemonDetailResponse
import com.example.pokedex.data.remote.model.PokemonListResponse
import com.example.pokedex.data.remote.model.PokemonResultItem
import com.example.pokedex.data.remote.model.PokemonTypeItem
import com.example.pokedex.data.remote.model.PokemonTypeSlot
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PokemonRepositoryImplTest {
    private lateinit var api: PokeApiService
    private lateinit var db: PokedexDatabase
    private lateinit var dao: PokemonDao
    private lateinit var repository: PokemonRepositoryImpl

    @Before
    fun setUp() {
        api = mockk()
        db = mockk(relaxed = true)
        dao = mockk(relaxed = true)
        repository =
            PokemonRepositoryImpl(
                api = api,
                db = db,
                dao = dao,
                dispatchers = TestDispatcherProvider(),
            )
    }

    @Test
    fun `getPokemonList returns local data when not empty and not force refresh`() =
        runTest {
            val mockEntities =
                listOf(
                    PokemonEntity(
                        id = 1,
                        name = "Bulbasaur",
                        imageUrl = "",
                        types = "",
                        height = 7,
                        weight = 69,
                        isFavorite = false,
                        stats = "",
                    ),
                )
            coEvery { dao.getPokemonList(any(), any()) } returns mockEntities

            val result = repository.getPokemonList(limit = 1, offset = 0, forceRefresh = false)

            assertTrue(result.isSuccess)
            assertEquals(1, result.getOrNull()?.size)
            assertEquals("Bulbasaur", result.getOrNull()?.first()?.name)
            coVerify(exactly = 0) { api.getPokemonList(any(), any()) }
        }

    @Test
    fun `getPokemonList fetches from network when local is empty`() =
        runTest {
            coEvery { dao.getPokemonList(any(), any()) } returns emptyList()
            coEvery { dao.getPokemonById(any()) } returns null
            coEvery { api.getPokemonList(any(), any()) } returns
                PokemonListResponse(
                    results = listOf(PokemonResultItem(name = "bulbasaur", url = "url/1/")),
                )
            coEvery { api.getPokemonDetail(any()) } returns
                PokemonDetailResponse(
                    id = 1,
                    name = "bulbasaur",
                    types = listOf(PokemonTypeSlot(slot = 1, type = PokemonTypeItem(name = "grass", url = ""))),
                    height = 7,
                    weight = 69,
                    stats = emptyList(),
                )

            val result = repository.getPokemonList(limit = 1, offset = 0, forceRefresh = false)

            assertTrue(result.isSuccess)
            assertEquals(1, result.getOrNull()?.size)
            assertEquals("bulbasaur", result.getOrNull()?.first()?.name)
            coVerify(exactly = 1) { api.getPokemonList(any(), any()) }
            coVerify(exactly = 1) { dao.insertAll(any()) }
        }
}
