// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  id("com.github.ben-manes.versions") version "0.51.0"
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.compose.compiler) apply false
  alias(libs.plugins.kotlin.serialization) apply false
  alias(libs.plugins.hilt.android) apply false
  alias(libs.plugins.ksp) apply false
  alias(libs.plugins.detekt) apply true
  alias(libs.plugins.ktlint) apply true
  alias(libs.plugins.android.library) apply false
}