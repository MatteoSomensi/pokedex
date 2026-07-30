plugins {
    id("pokedex.android.library")
    id("pokedex.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.pokedex.data"
    buildFeatures {
        buildConfig = true
    }
    sourceSets {
        named("androidTest") {
            assets.directories.add("schemas")
        }
    }
    testOptions {
        managedDevices {
            localDevices {
                create("pixel2Api35") {
                    device = "Pixel 2"
                    sdkVersion = 35
                    systemImageSource = "aosp"
                }
            }
        }
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:common"))
    implementation(libs.bundles.retrofit)
    implementation(libs.okhttp.logging)
    implementation(libs.androidx.security.crypto)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.bundles.room)
    ksp(libs.room.compiler)
    implementation(libs.androidx.room.paging)
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.common)

    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)
    implementation(libs.kotlinx.coroutines.play.services)

    implementation(libs.bundles.workmanager)
    ksp(libs.androidx.hilt.compiler)

    implementation(libs.coil)
    testImplementation(libs.bundles.test)
    testImplementation(libs.androidx.work.testing)
    testImplementation(libs.mockk.android)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.androidx.test.ext.junit)
    testImplementation(libs.okhttp.mockwebserver)

    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.room.testing)
    androidTestImplementation(libs.coroutines.test)
}
