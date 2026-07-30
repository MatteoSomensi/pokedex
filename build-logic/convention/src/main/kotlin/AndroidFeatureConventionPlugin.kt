import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("pokedex.android.library")
            pluginManager.apply("pokedex.android.hilt")
            pluginManager.apply("pokedex.android.compose")

        }
    }
}
