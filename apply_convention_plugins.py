import os

def overwrite_file(path, content):
    with open(path, 'w') as f:
        f.write(content.strip() + "\n")

# core/build.gradle.kts
overwrite_file('core/build.gradle.kts', """
plugins {
    id("pokedex.android.library")
    id("pokedex.android.compose")
    id("pokedex.android.hilt")
}

android {
    namespace = "com.example.pokedex.core"
}

dependencies {
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.iconsCore)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
}
""")

# domain/build.gradle.kts
overwrite_file('domain/build.gradle.kts', """
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
""")

# data/build.gradle.kts
overwrite_file('data/build.gradle.kts', """
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
""")

# feature_pokemon_list/build.gradle.kts
overwrite_file('feature_pokemon_list/build.gradle.kts', """
plugins {
    id("pokedex.android.feature")
}

android {
    namespace = "com.example.pokedex.feature.pokemonlist"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core"))
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.coil.compose)
}
""")

# feature_pokemon_detail/build.gradle.kts
overwrite_file('feature_pokemon_detail/build.gradle.kts', """
plugins {
    id("pokedex.android.feature")
}

android {
    namespace = "com.example.pokedex.feature.pokemondetail"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core"))
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.coil.compose)
}
""")

# app/build.gradle.kts
overwrite_file('app/build.gradle.kts', """
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("pokedex.android.compose")
    id("pokedex.android.hilt")
}

android {
    namespace = "com.example.pokedex"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.pokedex"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":core"))
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":feature_pokemon_list"))
    implementation(project(":feature_pokemon_detail"))

    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.compose.material3.adaptive.navigation3)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.turbine)
    
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
""")

print("All module build.gradle.kts refactored.")
