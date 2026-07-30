import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.example.pokedex.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    implementation(libs.roborazzi.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "pokedex.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "pokedex.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidFeature") {
            id = "pokedex.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("androidHilt") {
            id = "pokedex.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("androidCompose") {
            id = "pokedex.android.compose"
            implementationClass = "AndroidComposeConventionPlugin"
        }
        register("androidRoborazzi") {
            id = "pokedex.android.roborazzi"
            implementationClass = "AndroidRoborazziConventionPlugin"
        }
    }
}
