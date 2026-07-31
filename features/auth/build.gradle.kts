plugins {
    id("pokedex.android.feature")
}

android {
    namespace = "com.example.pokedex.feature.auth"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.bundles.credentials)

    implementation(libs.bundles.compose)
}
