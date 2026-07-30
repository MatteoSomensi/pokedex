import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
            with(pluginManager) {
                apply("com.android.library")
            }

            dependencies {
                add("testRuntimeOnly", libs.findLibrary("junit-vintage-engine").get())
                add("testRuntimeOnly", libs.findLibrary("junit-platform-launcher").get())
                add("testRuntimeOnly", libs.findLibrary("junit-jupiter-engine").get())
            }

            extensions.configure<LibraryExtension> {
                compileSdk = 37

                defaultConfig {
                    minSdk = 24
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }

                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_21
                    targetCompatibility = JavaVersion.VERSION_21
                }
            }

            tasks.withType<KotlinCompile>().configureEach {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_21)
                    freeCompilerArgs.addAll(
                        "-opt-in=kotlinx.serialization.InternalSerializationApi"
                    )
                }
            }

            tasks.withType<Test>().configureEach {
                useJUnitPlatform()
            }
        }
    }
}
