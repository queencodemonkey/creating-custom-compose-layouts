plugins {
  `kotlin-dsl`
}

group = "rt.build-logic"
version = "1.0-SNAPSHOT"

java {
  val javaVersionInt = libs.versions.java.get().toInt()
  val javaVersion = JavaVersion.toVersion(javaVersionInt)
  sourceCompatibility = javaVersion
  targetCompatibility = javaVersion

  toolchain {
    languageVersion.set(JavaLanguageVersion.of(javaVersionInt))
  }
}

dependencies {
  compileOnly(libs.gradle.plugin.android)
  compileOnly(libs.gradle.plugin.kotlin)
  implementation(libs.gradle.plugin.kotlinx.serialization)
  implementation(libs.gradle.plugin.detekt)
  implementation(libs.gradle.plugin.ktlint)
  implementation(libs.gradle.plugin.versionCatalogUpdate)
  implementation(libs.gradle.plugin.versionsBenManes)
  testImplementation(platform("org.junit:junit-bom:5.9.1"))
  testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
  useJUnitPlatform()
}


gradlePlugin {
  plugins {
    // region == Ecosystem Plugins ==
    register("androidLibrary") {
      id = "rt.android.library"
      implementationClass = "rt.convention.ecosystem.AndroidLibraryConventionPlugin"
    }
    register("androidLibraryCompose") {
      id = "rt.android.library.compose"
      implementationClass = "rt.convention.ecosystem.AndroidLibraryComposeConventionPlugin"
    }
    register("androidApplication") {
      id = "rt.android.application"
      implementationClass = "rt.convention.ecosystem.AndroidApplicationConventionPlugin"
    }
    register("androidApplicationCompose") {
      id = "rt.android.application.compose"
      implementationClass = "rt.convention.ecosystem.AndroidApplicationComposeConventionPlugin"
    }
    // endregion

    // region == Library Plugins ==
    register("kotlinSerializationJson") {
      id = "rt.kotlin.serialization.json"
      implementationClass = "rt.convention.library.KotlinSerializationJsonConventionPlugin"
    }
    register("hilt") {
      id = "rt.hilt"
      implementationClass = "rt.convention.library.di.HiltConventionPlugin"
    }
    // endregion

    // region == Plugin Configuration/Customizations ==
    register("detekt") {
      id = "rt.detekt"
      implementationClass = "rt.convention.library.codehealth.DetektConventionPlugin"
    }
    register("ktlint") {
      id = "rt.ktlint"
      implementationClass = "rt.convention.library.codehealth.KtlintConventionPlugin"
    }
    // endregion
  }
}