plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.hilt.android)
  alias(libs.plugins.ksp)
  alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.pokedex.data"
    compileSdk = 37
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

}

dependencies {
  implementation(libs.hilt.android)
  ksp(libs.hilt.compiler)
    implementation(project(":domain"))
  implementation(libs.retrofit)
  implementation(libs.retrofit.kotlinx.serialization)
  implementation(libs.okhttp.logging)
  implementation(libs.kotlinx.serialization.json)

}

kotlin {
    jvmToolchain(17)
}
