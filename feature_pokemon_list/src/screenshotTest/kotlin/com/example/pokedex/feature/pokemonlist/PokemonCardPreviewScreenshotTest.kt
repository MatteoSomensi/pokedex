package com.example.pokedex.feature.pokemonlist

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.example.pokedex.core.designsystem.components.PokemonCard
import com.example.pokedex.domain.model.Pokemon
import com.example.pokedex.theme.PokedexTheme

@PreviewTest
@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PokemonCardPreview() {
    val dummyPokemon = Pokemon(
        id = 1,
        name = "bulbasaur",
        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png",
        cryUrl = "https://raw.githubusercontent.com/PokeAPI/cries/main/cries/pokemon/latest/1.ogg",
        types = listOf("grass", "poison")
    )
    PokedexTheme {
        PokemonCard(pokemon = dummyPokemon, onClick = {})
    }
}
