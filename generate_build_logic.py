import os

def create_file(path, content):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, 'w') as f:
        f.write(content.strip() + "\n")

# 1. build-logic/settings.gradle.kts
create_file('build-logic/settings.gradle.kts', """
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}
rootProject.name = "build-logic"
include(":convention")
""")

# 2. build-logic/convention/build.gradle.kts
create_file('build-logic/convention/build.gradle.kts', """
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.example.pokedex.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    compileOnly(libs.findLibrary("android-gradlePlugin").getOrElse {
        dependencies.create("com.android.tools.build:gradle:9.1.0")
    })
    compileOnly(libs.findLibrary("kotlin-gradlePlugin").getOrElse {
        dependencies.create("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.10")
    })
}

gradlePlugin {
    plugins {
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
    }
}
""")

# 3. AndroidLibraryConventionPlugin.kt
create_file('build-logic/convention/src/main/kotlin/AndroidLibraryConventionPlugin.kt', """
import com.android.build.gradle.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<LibraryExtension> {
                compileSdk = 37

                defaultConfig {
                    minSdk = 24
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }

                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }
            }

            tasks.withType<KotlinCompile>().configureEach {
                kotlinOptions {
                    jvmTarget = "17"
                }
            }
        }
    }
}
""")

# 4. AndroidFeatureConventionPlugin.kt
create_file('build-logic/convention/src/main/kotlin/AndroidFeatureConventionPlugin.kt', """
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("pokedex.android.library")
            pluginManager.apply("pokedex.android.hilt")
            pluginManager.apply("pokedex.android.compose")

            dependencies {
                // Here we would typically add common feature dependencies
                // like ViewModel, Navigation, etc. if we can resolve the catalog here.
            }
        }
    }
}
""")

# 5. AndroidComposeConventionPlugin.kt
create_file('build-logic/convention/src/main/kotlin/AndroidComposeConventionPlugin.kt', """
import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            extensions.configure<CommonExtension<*, *, *, *, *, *>> {
                buildFeatures {
                    compose = true
                }
            }
        }
    }
}
""")

# 6. AndroidHiltConventionPlugin.kt
create_file('build-logic/convention/src/main/kotlin/AndroidHiltConventionPlugin.kt', """
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.google.devtools.ksp")
                apply("com.google.dagger.hilt.android")
            }

            dependencies {
                add("implementation", "com.google.dagger:hilt-android:2.60.1")
                add("ksp", "com.google.dagger:hilt-compiler:2.60.1")
            }
        }
    }
}
""")

print("build-logic skeleton created.")
