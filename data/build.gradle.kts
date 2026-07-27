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
    implementation(project(":core"))
    implementation(libs.bundles.retrofit)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlinx.serialization.json)

    // Room
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)

    // Auth Dependencies
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.kotlinx.coroutines.play.services)
    
    // WorkManager
    implementation(libs.bundles.workmanager)
    ksp(libs.androidx.hilt.compiler)
    
    // Coil (Image caching)
    implementation(libs.coil)
    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    testImplementation("androidx.work:work-testing:2.9.0")
    testImplementation("io.mockk:mockk-android:1.13.11") // To mock android classes if needed
    testImplementation("org.robolectric:robolectric:4.11.1")
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.androidx.test.ext.junit)
}
