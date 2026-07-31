import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension

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
    alias(libs.plugins.cyclonedx) apply false
}

abstract class VerifyModuleBoundariesTask : DefaultTask() {
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val moduleBuildFiles: ConfigurableFileCollection

    @TaskAction
    fun verify() {
        val violations = mutableListOf<String>()
        moduleBuildFiles.files.forEach { buildFile ->
            val normalizedPath = buildFile.path.replace('\\', '/')
            val dependencies =
                Regex("""project\("(:[^"]+)"\)""")
                    .findAll(buildFile.readText())
                    .map { match -> match.groupValues[1] }
                    .toSet()

            fun forbid(vararg prefixes: String) {
                dependencies
                    .filter { dependency -> prefixes.any(dependency::startsWith) }
                    .forEach { dependency ->
                        violations += "${buildFile.parentFile.name} must not depend on $dependency"
                    }
            }

            when {
                normalizedPath.endsWith("/core/domain/build.gradle.kts") ->
                    forbid(":app", ":core:", ":features:")
                normalizedPath.endsWith("/core/data/build.gradle.kts") ->
                    forbid(":app", ":core:designsystem", ":features:")
                normalizedPath.endsWith("/core/designsystem/build.gradle.kts") ->
                    forbid(":app", ":core:data", ":core:domain", ":features:")
            }
        }

        check(violations.isEmpty()) {
            "Module boundary violations:\n${violations.joinToString(separator = "\n")}"
        }
    }
}

tasks.register<VerifyModuleBoundariesTask>("verifyModuleBoundaries") {
    group = "verification"
    description = "Fails when core modules cross the documented dependency boundaries."
    moduleBuildFiles.from(
        fileTree(layout.projectDirectory) {
            include("core/*/build.gradle.kts")
            include("features/*/build.gradle.kts")
            include("features/*/*/build.gradle.kts")
        },
    )
}

subprojects {
    pluginManager.apply("io.gitlab.arturbosch.detekt")
    pluginManager.apply("org.jlleitschuh.gradle.ktlint")
    pluginManager.apply("jacoco")

    extensions.configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        baseline = file("$projectDir/detekt-baseline.xml")
    }
    extensions.configure<JacocoPluginExtension> {
        toolVersion = "0.8.13"
    }
}

val documentedProjectPaths =
    listOf(
        ":app",
        ":core:common",
        ":core:designsystem",
        ":core:domain",
        ":core:data",
        ":features:auth",
        ":features:favorite:api",
        ":features:favorite:impl",
        ":features:pokemon_detail",
        ":features:pokemon_list",
        ":macrobenchmark",
    )

configure(documentedProjectPaths.map(::project)) {
    pluginManager.apply("org.jetbrains.dokka")
}

dependencies {
    documentedProjectPaths.forEach { documentedProjectPath ->
        dokka(project(documentedProjectPath))
    }
}
