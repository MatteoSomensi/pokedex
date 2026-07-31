pluginManagement {
    includeBuild("build-logic")
    @Suppress("UnstableApiUsage")
    repositories {
        google {
            content {
                includeGroupByRegex("androidx.*")
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    @Suppress("UnstableApiUsage")
    repositories {
        google {
            content {
                includeGroupByRegex("androidx.*")
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "Pokedex"
include(":app")
include(":core:common")
include(":core:domain")
include(":core:data")
include(":features:pokemon_list")
include(":features:pokemon_detail")
include(":features:auth")
include(":macrobenchmark")
include(":features:favorite:api")
include(":features:favorite:impl")
include(":core:designsystem")
