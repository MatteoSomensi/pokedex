plugins {
    id("pokedex.android.library")
    id("pokedex.android.compose")
}

android {
    namespace = "com.example.pokedex.core.designsystem"
}

dependencies {

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.iconsCore)
    implementation(libs.coil.compose)
}
