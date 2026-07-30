import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

plugins {
    id("pokedex.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.pokedex.feature.favorite.api"
}

@CacheableTask
abstract class VerifyApiContractTask : DefaultTask() {
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val apiSources: ConfigurableFileCollection

    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val referenceContract: RegularFileProperty

    @TaskAction
    fun verify() {
        val actual =
            apiSources.files
                .sortedBy { source -> source.path }
                .joinToString(separator = "\n") { source ->
                    source
                        .readText()
                        .replace(Regex(pattern = """/\*.*?\*/""", option = RegexOption.DOT_MATCHES_ALL), "")
                        .lineSequence()
                        .map { line -> line.substringBefore("//").trim() }
                        .filter(String::isNotEmpty)
                        .joinToString(separator = "\n")
                }.trim()
        val expected =
            referenceContract
                .get()
                .asFile
                .readText()
                .trim()

        check(actual == expected) {
            "Public favorite API changed. Review it, then update api/current.txt intentionally."
        }
    }
}

val verifyApiContract =
    tasks.register<VerifyApiContractTask>("verifyApiContract") {
        group = "verification"
        description = "Checks the public favorite source contract against its reviewed snapshot."
        apiSources.from(fileTree("src/main") { include("**/*.kt") })
        referenceContract.set(layout.projectDirectory.file("api/current.txt"))
    }

tasks.named("check").configure {
    dependsOn(verifyApiContract)
}

dependencies {
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    implementation(libs.bundles.compose)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.kotlinx.serialization.json)
}
