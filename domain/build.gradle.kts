plugins {
    id("pokedex.android.library")
    id("pokedex.android.hilt")
}

android {
    namespace = "com.example.pokedex.domain"
}

dependencies {
    implementation(project(":core"))
    implementation(libs.kotlinx.coroutines.test)
}
