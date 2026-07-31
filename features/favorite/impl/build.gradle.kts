plugins {
    id("pokedex.android.feature")
}

android {
    namespace = "com.example.pokedex.feature.favorite.impl"
}

dependencies {
    implementation(project(":features:favorite:api"))
    implementation(project(":core:domain"))
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.bundles.compose)
}
