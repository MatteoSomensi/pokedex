plugins {
    id("pokedex.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.pokedex.feature.favorite.api"
}

dependencies {
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    implementation(libs.bundles.compose)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.kotlinx.serialization.json)
}
