package com.example.pokedex.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pokedex.domain.model.Pokemon

/**
 * Cached Room representation of a [Pokemon].
 *
 * Collection-valued fields use a compact string encoding to keep the teaching project free from
 * custom Room type converters. [toDomain] and [fromDomain] form the persistence boundary.
 */
@Entity(tableName = "pokemon")
data class PokemonEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String,
    val types: String,
    val height: Int,
    val weight: Int,
    val stats: String,
    val isFavorite: Boolean = false,
) {
    /** Converts the persisted record to the model exposed by the domain layer. */
    fun toDomain(): Pokemon =
        Pokemon(
            id = id,
            name = name,
            imageUrl = imageUrl,
            types = types.split(",").filter { it.isNotBlank() },
            height = height,
            weight = weight,
            stats = parseStats(stats),
            cryUrl = "https://raw.githubusercontent.com/PokeAPI/cries/main/cries/pokemon/latest/$id.ogg",
            isFavorite = isFavorite,
        )

    companion object {
        /** Creates a persistence record while preserving the domain favorite state. */
        fun fromDomain(pokemon: Pokemon): PokemonEntity =
            PokemonEntity(
                id = pokemon.id,
                name = pokemon.name,
                imageUrl = pokemon.imageUrl,
                types = pokemon.types.joinToString(","),
                height = pokemon.height,
                weight = pokemon.weight,
                stats = formatStats(pokemon.stats),
                isFavorite = pokemon.isFavorite,
            )

        private fun formatStats(stats: Map<String, Int>): String = stats.entries.joinToString(";") { "${it.key}:${it.value}" }

        private fun parseStats(stats: String): Map<String, Int> {
            if (stats.isBlank()) return emptyMap()
            return stats.split(";").associate {
                val parts = it.split(":")
                parts[0] to parts[1].toInt()
            }
        }
    }
}
