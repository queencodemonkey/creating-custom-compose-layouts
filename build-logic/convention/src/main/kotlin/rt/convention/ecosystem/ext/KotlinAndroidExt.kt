/*
 * MIT License
 *
 * Copyright (c) 2024 Randomly Typing
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package rt.convention.ecosystem.ext

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.provideDelegate
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import rt.convention.ext.library
import rt.convention.ext.version
import rt.convention.ext.versionCatalog
import rt.convention.ext.versionInt

//
// This file contains extension methods for configuring Kotlin options
// in Android plugins.
//

/**
 * Configure Android ecosystem plugins, including Kotlin options.
 *
 * Adapted from Now in Android:
 * https://github.com/android/nowinandroid/blob/main/build-logic/convention/src/main/kotlin/com/google/samples/apps/nowinandroid/KotlinAndroid.kt#L32
 *
 * @param commonExtension reference to the common properties of the Android plugins (application, library, etc.)
 * @param versionCatalog name of the version catalog to use to resolve versions, deps, plugins, etc.;
 * defaults to name of default version catalog: "libs".
 * Reference: https://docs.gradle.org/current/userguide/platforms.html#sub:conventional-dependencies-toml
 */
internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    versionCatalog: String = "libs",
) {
    val libs = versionCatalog(versionCatalog)

    commonExtension.apply {
        val java = libs.version("java")
        val javaVersion = JavaVersion.toVersion(java)

        compileSdk = libs.versionInt("compileSdk")

        defaultConfig {
            minSdk = libs.versionInt("minSdk")
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

            vectorDrawables {
                useSupportLibrary = true
            }
        }

        compileOptions {
            sourceCompatibility = javaVersion
            targetCompatibility = javaVersion
            isCoreLibraryDesugaringEnabled = true
        }

        configure<KotlinAndroidProjectExtension> {
          sourceSets.all {
            languageSettings.enableLanguageFeature("ExplicitBackingFields")
          }
            val warningsAsErrors: String? by project
            compilerOptions {
                // Treat all Kotlin warnings as errors (disabled by default)
                // Override by setting warningsAsErrors=true in your ~/.gradle/gradle.properties
                allWarningsAsErrors.set(warningsAsErrors.toBoolean())

                // https://kotlinlang.org/docs/gradle-compiler-options.html#common-attributes
                optIn.add("kotlin.ExperimentalStdlibApi")

                jvmTarget.set(JvmTarget.fromTarget(java))
            }
        }
    }

    dependencies {
        add("implementation", libs.library("androidx-core-ktx"))

        // https://developer.android.com/studio/write/java8-support#library-desugaring
        add("coreLibraryDesugaring", libs.findLibrary("android.desugarJdkLibs").get())
    }
}
