plugins {
    id("pokedex.android.application")
    id("pokedex.android.compose")
    id("pokedex.android.hilt")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.androidx.baselineprofile)
    alias(libs.plugins.cyclonedx)
}

tasks.named("cyclonedxDirectBom") {
    group = "verification"
}

val firebaseConfigured = file("google-services.json").isFile
val firebaseEnabled =
    providers
        .gradleProperty("FIREBASE_ENABLED")
        .map(String::toBoolean)
        .orElse(false)
        .get()
if (firebaseConfigured) {
    pluginManager.apply("com.google.gms.google-services")
    pluginManager.apply("com.google.firebase.crashlytics")
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
            "\"${project.findProperty("WEB_CLIENT_ID") ?: ""}\"",
        )
        buildConfigField("boolean", "FIREBASE_CONFIGURED", (firebaseConfigured && firebaseEnabled).toString())
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

    testOptions {
        @Suppress("UnstableApiUsage")
        managedDevices {
            localDevices {
                create("pixel2Api35") {
                    device = "Pixel 2"
                    sdkVersion = 35
                    systemImageSource = "aosp"
                    testedAbi = "x86_64"
                }
            }
        }
    }
}

ksp {
    arg("appfunctions:aggregateAppFunctions", "true")
}

dependencies {
    implementation(libs.androidx.appfunctions)
    implementation(libs.androidx.appfunctions.service)
    ksp(libs.androidx.appfunctions.compiler)

    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)

    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":features:pokemon_list"))
    implementation(project(":features:pokemon_detail"))
    implementation(project(":features:auth"))
    implementation(project(":features:favorite:api"))
    implementation(project(":features:favorite:impl"))
    implementation(libs.kotlinx.serialization.json)


    implementation(libs.bundles.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.compose.material3.adaptive.navigation3)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)

    testImplementation(libs.bundles.test)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)

    androidTestImplementation(libs.bundles.android.test)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.uiautomator)


    implementation(libs.bundles.workmanager)
    ksp(libs.androidx.hilt.compiler)

    implementation(libs.androidx.profileinstaller)
    "baselineProfile"(project(":macrobenchmark"))
}
