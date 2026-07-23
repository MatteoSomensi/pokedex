plugins {
    id("pokedex.android.feature")
}

android {
    namespace = "com.example.pokedex.feature.auth"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core"))
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    
    // Credential Manager for Google Sign-in
    implementation(libs.bundles.credentials)
    
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    implementation(libs.bundles.compose)
}
