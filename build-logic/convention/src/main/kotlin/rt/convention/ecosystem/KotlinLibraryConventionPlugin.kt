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

package rt.convention.ecosystem

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import rt.convention.ext.getPluginId
import rt.convention.ext.version
import rt.convention.ext.versionCatalog
import rt.convention.ext.versionInt

/**
 * Convention Plugin for providing common Kotlin library configuration.
 *
 * References:
 * https://docs.gradle.org/current/samples/sample_convention_plugins.html
 */
@Suppress("unused")
class KotlinLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs = versionCatalog()

            with(pluginManager) {
                apply("java-library")
                apply(libs.getPluginId("kotlin-jvm"))
            }

            val javaVersionInt = libs.versionInt("java")
            val javaVersion = JavaVersion.toVersion(javaVersionInt)

            extensions.configure<JavaPluginExtension> {
                sourceCompatibility = javaVersion
                targetCompatibility = javaVersion

                toolchain {
                    languageVersion.set(JavaLanguageVersion.of(javaVersionInt))
                }
            }

            dependencies {
                add("testImplementation", kotlin("test"))
            }

            tasks.named("compileKotlin", org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask::class.java) {
                compilerOptions {
                    // Treat all Kotlin warnings as errors (disabled by default)
                    // Override by setting warningsAsErrors=true in your ~/.gradle/gradle.properties
                    val warningsAsErrors: String? by project
                    allWarningsAsErrors.set(warningsAsErrors.toBoolean())
                    // https://kotlinlang.org/docs/gradle-compiler-options.html#how-to-define-options
                    optIn.addAll(
                        listOf(
                            "kotlin.ExperimentalStdlibApi",
                            "kotlin.serialization.InternalSerializationApi",
                            "kotlin.serialization.ExperimentalSerializationApi",
                        ),
                    )
                    apiVersion.set(KotlinVersion.fromVersion(libs.version("kotlin-version")))
                }
            }
        }
    }
}
