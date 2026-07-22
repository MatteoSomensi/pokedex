plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.hilt.android)
  alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.pokedex.domain"
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
  
    implementation(project(":core"))

}

kotlin {
    jvmToolchain(17)
}
