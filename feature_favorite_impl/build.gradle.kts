plugins {
    id("pokedex.android.feature")
}

android {
    namespace = "com.example.pokedex.feature.favorite.impl"
}

dependencies {
    implementation(project(":feature_favorite_api"))
    implementation(project(":domain"))
}
