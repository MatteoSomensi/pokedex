// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.version.catalog.update)
    alias(libs.plugins.ben.manes.versions)
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.detekt) apply true
    alias(libs.plugins.ktlint) apply true
    alias(libs.plugins.dokka)
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.androidx.baselineprofile) apply false
    alias(libs.plugins.roborazzi) apply false
    alias(libs.plugins.screenshot) apply false
}

subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    extensions.configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        baseline = file("$projectDir/detekt-baseline.xml")
    }
}

val documentedProjects =
    listOf(
        project(":app"),
        project(":core:common"),
        project(":core:designsystem"),
        project(":core:domain"),
        project(":core:data"),
        project(":features:auth"),
        project(":features:favorite:api"),
        project(":features:favorite:impl"),
        project(":features:pokemon_detail"),
        project(":features:pokemon_list"),
        project(":macrobenchmark"),
    )

configure(documentedProjects) {
    apply(plugin = "org.jetbrains.dokka")
}

dependencies {
    documentedProjects.forEach { documentedProject ->
        dokka(documentedProject)
    }
}
