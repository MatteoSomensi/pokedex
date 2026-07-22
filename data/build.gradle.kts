plugins {
    id("pokedex.android.library")
    id("pokedex.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.pokedex.data"
}

dependencies {
    implementation(project(":domain"))
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlinx.serialization.json)
}
