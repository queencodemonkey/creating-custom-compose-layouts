// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  alias(libs.plugins.android.app) apply false
  alias(libs.plugins.android.lib) apply false
  alias(libs.plugins.kotlin.android) apply false
  alias(libs.plugins.kotlin.compose) apply false
  alias(libs.plugins.hilt) apply false
  alias(libs.plugins.ksp) apply false

  // Apply dependency management/utilities at this top level so we can
  // run these tools from the root.
  alias(libs.plugins.dependencyAnalysisAndroid)
  id("rt.detekt")
  id("rt.ktlint")
}
