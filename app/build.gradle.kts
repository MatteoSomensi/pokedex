plugins {
    id("pokedex.android.application")
    id("com.google.devtools.ksp")
    id("pokedex.android.compose")
    id("pokedex.android.hilt")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.example.pokedex"
    defaultConfig {
        applicationId = "com.example.pokedex"
        versionCode = (project.findProperty("VERSION_CODE") as? String)?.toIntOrNull() ?: 1
        versionName = (project.findProperty("VERSION_NAME") as? String) ?: "1.0"

        buildConfigField(
            "String",
            "WEB_CLIENT_ID",
            "\"${project.findProperty("WEB_CLIENT_ID") ?: ""}\""
        )
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        create("benchmark") {
            initWith(getByName("release"))
            matchingFallbacks += listOf("release")
            isDebuggable = false
            proguardFiles("benchmark-rules.pro")
        }
    }

    ksp {
        arg("appfunctions:aggregateAppFunctions", "true")
    }
}



dependencies {
    implementation(libs.androidx.appfunctions)
    implementation(libs.androidx.appfunctions.service)
    ksp(libs.androidx.appfunctions.compiler)

    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)

    implementation(project(":core"))
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":feature_pokemon_list"))
    implementation(project(":feature_pokemon_detail"))
    implementation(project(":feature_auth"))
    implementation(project(":feature_favorite_api"))
    implementation(project(":feature_favorite_impl"))
    implementation(libs.kotlinx.serialization.json)

    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation(libs.bundles.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.compose.material3.adaptive.navigation3)

    testImplementation(libs.bundles.test)

    androidTestImplementation(libs.bundles.android.test)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    
    // WorkManager
    implementation(libs.bundles.workmanager)
    ksp(libs.androidx.hilt.compiler)

    implementation(libs.androidx.profileinstaller)
}
