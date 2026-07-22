import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            val extension = extensions.findByName("android")
            if (extension is ApplicationExtension) {
                extension.buildFeatures {
                    compose = true
                }
            } else if (extension is LibraryExtension) {
                extension.buildFeatures {
                    compose = true
                }
            }
        }
    }
}
