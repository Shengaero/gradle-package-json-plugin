/*
 * Copyright 2018 Kaidan Gustave
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:Suppress("RemoveEmptyPrimaryConstructor", "unused")
package me.kgustave.gradle.pkg.json.plugin

import groovy.lang.Closure
import kotlinx.serialization.json.JSON
import me.kgustave.gradle.pkg.json.internal.Open
import me.kgustave.gradle.pkg.json.internal.PkgJsonSerializer
import me.kgustave.gradle.pkg.json.internal.getValue
import me.kgustave.gradle.pkg.json.plugin.conventions.PkgJsonCacheConvention
import me.kgustave.gradle.pkg.json.plugin.conventions.PkgJsonConvention
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.Incubating
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

/**
 * Task to generate a package.json file.
 */
@Open class PkgJsonTask @Inject constructor(private val json: JSON): DefaultTask() {

    /**
     * The name of the output file.
     *
     * Default: `package.json`
     */
    @[Optional Input]
    var outputName: String = "package.json"

    /**
     * The name of the output directory, relative to the project directory.
     *
     * Default: empty string
     */
    @[Optional Input]
    var outputDirectory: String = ""

    /**
     * Allows the package.json plugin to automatically execute
     * when build is refreshed.
     *
     * **Note** - Auto updating is an __experimental__ feature and
     * may be removed or have breaking changes made between releases.
     */
    @Incubating
    @[Optional Input]
    var autoUpdateFile: Boolean = false

    init {
        group = System.getProperty("package.json.task.group") ?: "package json"
        description = "Updates the 'package.json' file for the project."

        with(extensions) {
            add(PkgJsonConvention.NAME, PkgJsonConvention::class.java)
            add(PkgJsonCacheConvention.NAME, PkgJsonCacheConvention::class.java)
        }
    }

    ////////////////
    // EXTENSIONS //
    ////////////////

    /**
     * The [package json convention][PkgJsonConvention] of this task.
     */
    @get:[Optional Input]
    val pkg: PkgJsonConvention by extensions

    /**
     * The [cache convention][PkgJsonCacheConvention] of this task.
     */
    @Incubating
    @get:[Optional Input]
    val cache: PkgJsonCacheConvention by extensions

    /**
     * Configures the [package json convention][pkg] for this task.
     */
    fun pkg(action: Action<in PkgJsonConvention>) {
        action.execute(pkg)
    }

    /**
     * Configures the [package json convention][pkg] for this task.
     */
    fun pkg(closure: Closure<Unit>) {
        closure.apply { delegate = pkg }.call()
    }

    /**
     * Configures the [cache convention][cache] for this task.
     */
    @Incubating
    fun cache(action: Action<in PkgJsonCacheConvention>) {
        action.execute(cache)
    }

    /**
     * Configures the [cache convention][cache] for this task.
     */
    @Incubating
    fun cache(closure: Closure<Unit>) {
        closure.apply { delegate = cache }.call()
    }

    @Internal
    @TaskAction
    internal fun generate() {
        val packageJsonFile = project.file(outputName)
        if(!packageJsonFile.exists()) {
            check(packageJsonFile.createNewFile()) { "Could not create package.json file!" }
        }

        packageJsonFile.writeText(
            text = json.stringify(PkgJsonSerializer, pkg.toPkgJson()),
            charset = Charsets.UTF_8
        )
    }

    internal companion object {
        internal const val DEFAULT_NAME = "packageJson"
    }
}
