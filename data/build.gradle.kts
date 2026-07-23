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
    implementation(libs.bundles.retrofit)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlinx.serialization.json)

    // Auth Dependencies
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.bundles.credentials)
    implementation(libs.kotlinx.coroutines.play.services)
}
