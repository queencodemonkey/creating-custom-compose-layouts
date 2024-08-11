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
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import rt.convention.ext.getPluginId
import rt.convention.ext.library
import rt.convention.ext.versionCatalog

//
// This file contains extension methods for configuring Jetpack Compose
// in Android plugins.
//

/**
 * Configure Compose-specific options for a [Project] that has an
 * Android plugin (library, application, etc.) applied.
 *
 * Adapted from Now in Android:
 * https://github.com/android/nowinandroid/blob/main/build-logic/convention/src/main/kotlin/com/google/samples/apps/nowinandroid/AndroidCompose.kt
 */
internal fun Project.configureAndroidCompose(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    val libs = versionCatalog()

    with(pluginManager) {
        apply(libs.getPluginId("kotlin-compose"))
    }

    commonExtension.apply {
        buildFeatures {
            compose = true
        }
        dependencies {
            val bom = platform(libs.library("compose-bom"))
            add("implementation", bom)
            add("implementation", libs.library("compose-ui"))
            add("implementation", libs.library("compose-ui-graphics"))
            add("implementation", libs.library("compose-ui-tooling-preview"))
            add("debugImplementation", bom)
            add("debugImplementation", libs.library("compose-ui-tooling"))

            add("lintChecks", libs.library("lint-compose"))
        }
    }
}
