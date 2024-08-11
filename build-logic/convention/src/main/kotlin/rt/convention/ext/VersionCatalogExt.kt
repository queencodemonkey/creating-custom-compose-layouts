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

package rt.convention.ext

import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.provider.Provider
import org.gradle.plugin.use.PluginDependency

//
// This file contains extension methods related to `VersionCatalog` instances
//

/**
 * Returns a version from this Version Catalog that
 * matches [key] in cases where type-safe accessors cannot
 * be used.
 *
 * If [key] does not match any version, returns an empty
 * string.
 *
 * Generally, this extension is used for plugins since they
 * need to be resolved as part of building the catalog.
 */
internal fun VersionCatalog.version(key: String): String = findVersion(key).get().requiredVersion

/**
 * Returns the [Int] value of a version from this Version
 * Catalog that matches [key]; throws an exception if there
 * is no matching version.
 */
internal fun VersionCatalog.versionInt(key: String): Int {
    val version: String = version(key)
    require(version.isNotEmpty()) { "No version in $name catalog matching \"$key\"" }
    return version.toInt()
}

/**
 * Returns a dependency providers for the library in this
 * Version Catalog that matches [alias]; throws an exception
 */
internal fun VersionCatalog.library(alias: String): Provider<MinimalExternalModuleDependency> =
    findLibrary(alias).orElseThrow { error("No library in $name matching alias \"$alias\"") }

/**
 * Returns the plugin ID for the plugin from this Version
 * Catalog with the given [alias]; throws an exception if
 * there is no matching plugin.
 */
internal fun VersionCatalog.getPluginId(alias: String): String {
    val pluginProvider: Provider<PluginDependency> = findPlugin(alias).get()
    require(pluginProvider.isPresent) { "No plugin in $name catalog matching alias \"$alias\"" }
    return pluginProvider.get().pluginId
}
