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
