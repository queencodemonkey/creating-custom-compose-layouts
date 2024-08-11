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

package rt.convention.library

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import rt.convention.ext.getPluginId
import rt.convention.ext.library
import rt.convention.ext.versionCatalog

/**
 * Convention Plugin for providing common configuration for
 * the Kotlinx Serialization library and plugin:
 * https://github.com/Kotlin/kotlinx.serialization
 *
 * References:
 * https://docs.gradle.org/current/samples/sample_convention_plugins.html
 */
@Suppress("unused")
class KotlinSerializationJsonConventionPlugin : Plugin<Project> {
  override fun apply(target: Project) {
    with(target) {
      val libs = versionCatalog()

      with(pluginManager) {
        apply(libs.getPluginId("kotlin-serialization"))
      }

      configure<KotlinAndroidProjectExtension> {
        compilerOptions {
          optIn.add("kotlin.serialization.ExperimentalSerializationApi")
        }
      }

      dependencies {
        add("implementation", libs.library("kotlin-serialization-json"))
      }
    }
  }
}
