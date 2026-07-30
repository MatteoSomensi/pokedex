package com.example.pokedex.data.local

import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.pokedex.data.local.entity.PokemonEntity
import com.example.pokedex.data.local.entity.PokemonRemoteKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PokemonDaoTest {
    private lateinit var database: PokedexDatabase

    @Before
    fun createDatabase() {
        database =
            Room
                .inMemoryDatabaseBuilder(
                    ApplicationProvider.getApplicationContext(),
                    PokedexDatabase::class.java,
                ).allowMainThreadQueries()
                .build()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun favoriteAndQueryScopedPagingStateArePersisted() =
        runTest {
            val pokemon = pokemon(id = 1)
            database.pokemonDao().insert(pokemon)
            database.remoteKeyDao().insertAll(
                listOf(
                    PokemonRemoteKey(
                        pokemonId = pokemon.id,
                        query = "bulb",
                        prevOffset = null,
                        nextOffset = 20,
                    ),
                ),
            )

            database.pokemonDao().updateFavoriteStatus(id = pokemon.id, isFavorite = true)

            assertEquals(
                setOf(1),
                database
                    .pokemonDao()
                    .observeFavoritePokemonIds()
                    .first()
                    .toSet(),
            )
            assertEquals(
                20,
                database.remoteKeyDao().remoteKey(pokemonId = 1, query = "bulb")?.nextOffset,
            )
            val page =
                database
                    .pokemonDao()
                    .getPokemonPagingSource(query = "bulb")
                    .load(PagingSource.LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false))
            assertEquals(listOf(1), (page as PagingSource.LoadResult.Page).data.map { it.id })
        }

    private fun pokemon(id: Int) =
        PokemonEntity(
            id = id,
            name = "bulbasaur",
            imageUrl = "image",
            types = "Grass,Poison",
            height = 7,
            weight = 69,
            stats = "hp:45",
        )
}
