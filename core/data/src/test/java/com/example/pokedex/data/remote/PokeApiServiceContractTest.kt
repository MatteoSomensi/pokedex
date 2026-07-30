package com.example.pokedex.data.remote

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class PokeApiServiceContractTest {
    private lateinit var server: MockWebServer
    private lateinit var service: PokeApiService

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        val json =
            Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            }
        service =
            Retrofit
                .Builder()
                .baseUrl(server.url("/api/v2/"))
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
                .create(PokeApiService::class.java)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun listContractIncludesPaginationQueryAndIgnoresUnknownFields() =
        runTest {
            server.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody(
                        """
                        {
                          "count": 1302,
                          "results": [
                            {
                              "name": "bulbasaur",
                              "url": "https://pokeapi.co/api/v2/pokemon/1/"
                            }
                          ]
                        }
                        """.trimIndent(),
                    ),
            )

            val response = service.getPokemonList(limit = 20, offset = 40)

            assertEquals("bulbasaur", response.results.single().name)
            assertEquals("/api/v2/pokemon?limit=20&offset=40", server.takeRequest().path)
        }

    @Test
    fun detailContractMapsSnakeCaseStats() =
        runTest {
            server.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody(
                        """
                        {
                          "id": 1,
                          "name": "bulbasaur",
                          "height": 7,
                          "weight": 69,
                          "types": [
                            {
                              "slot": 1,
                              "type": {"name": "grass", "url": "type/12"}
                            }
                          ],
                          "stats": [
                            {
                              "base_stat": 45,
                              "stat": {"name": "hp"}
                            }
                          ]
                        }
                        """.trimIndent(),
                    ),
            )

            val response = service.getPokemonDetail("bulbasaur")

            assertEquals(45, response.stats.single().baseStat)
            assertEquals("/api/v2/pokemon/bulbasaur", server.takeRequest().path)
        }
}
