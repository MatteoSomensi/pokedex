plugins {
    id("pokedex.android.feature")
}

android {
    namespace = "com.example.pokedex.feature.pokemondetail"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core"))
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.coil.compose)
}
