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
@file:Suppress("unused", "UnstableApiUsage", "MemberVisibilityCanBePrivate", "DeprecatedCallableAddReplaceWith", "DEPRECATION")
package me.kgustave.gradle.pkg.json.plugin

import kotlinx.serialization.json.JsonTreeParser
import me.kgustave.gradle.pkg.json.plugin.conventions.PkgJsonCacheConvention
import me.kgustave.gradle.pkg.json.plugin.conventions.PkgJsonConvention
import me.kgustave.gradle.pkg.json.plugin.conventions.PkgJsonFormattingConvention
import me.kgustave.gradle.pkg.json.plugin.internal.data.PkgJson
import me.kgustave.gradle.pkg.json.plugin.internal.jsonObjectOf
import me.kgustave.gradle.pkg.json.plugin.internal.stringify
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.Incubating
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.getValue
import kotlin.reflect.full.memberProperties

/**
 * Task to generate a package.json file.
 */
open class PkgJsonTask: DefaultTask() {
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

    /**
     * Allows the package.json plugin to automatically configure it's dependency objects
     * via gradle's native [dependencies][org.gradle.api.Project.dependencies] DSL.
     *
     * **Note** - Dependency configurations are an __experimental__ feature and
     * may be removed or have breaking changes made between releases.
     */
    @Incubating
    @[Optional Input]
    var enableDependencyConfiguration: Boolean = false

    init {
        this.group = "package json"
        this.description = "Updates the 'package.json' file for the project."

        with(extensions) {
            create<PkgJsonCacheConvention>(PkgJsonCacheConvention.NAME)
            create<PkgJsonFormattingConvention>(PkgJsonFormattingConvention.NAME)
        }
    }

    ////////////////
    // EXTENSIONS //
    ////////////////

    /**
     * The [package json convention][PkgJsonConvention] of this task.
     */
    @get:[Optional Input]
    @Deprecated("use top level extension")
    val pkg: PkgJsonConvention get() = project.extensions.getByType()

    /**
     * Configures the [package json convention][pkg] for this task.
     */
    @Deprecated("use top level extension")
    fun pkg(action: Action<in PkgJsonConvention>) = action.execute(pkg)

    /**
     * The [formatting convention][PkgJsonFormattingConvention] of this task.
     */
    @get:[Optional Input]
    val formatting: PkgJsonFormattingConvention by extensions

    /**
     * Configures the [formatting convention][formatting] for this task.
     */
    fun formatting(action: Action<in PkgJsonFormattingConvention>) = action.execute(formatting)

    /**
     * The [cache convention][PkgJsonCacheConvention] of this task.
     */
    @Incubating
    @get:[Optional Input]
    val cache: PkgJsonCacheConvention by extensions

    /**
     * Configures the [cache convention][cache] for this task.
     */
    @Incubating fun cache(action: Action<in PkgJsonCacheConvention>) = action.execute(cache)

    @Internal @TaskAction internal fun generate() {
        val packageJsonFile = project.file(outputName)

        if(!packageJsonFile.exists()) {
            logger.debug("No package.json file found at $packageJsonFile")
            logger.debug("Creating new package.json file..")

            check(packageJsonFile.createNewFile()) { "Could not create package.json file!" }

            logger.debug("package.json file created at $packageJsonFile")
        }

        val result = runCatching { JsonTreeParser.parse(packageJsonFile.readText(Charsets.UTF_8)) }

        val oldJson = result.getOrNull() ?: jsonObjectOf()

        val extra = oldJson.asSequence()
            .filter { it.key !in pkgJsonProperties }
            .associate { it.key to it.value }

        if(extra.isNotEmpty()) {
            logger.debug("Discovered ${extra.size} extra properties: " +
                extra.keys.joinToString(", ", prefix = "[", postfix = "]"))
            logger.debug("These will be preserved.")
        }

        val newJson = project.extensions
            .getByType<PkgJsonConvention>().toPkgJson()
            .copy(extra = extra).toJson()

        val text = newJson.stringify(formatting.indentFactor)

        logger.debug("Text conversion complete:\n$text")
        logger.debug("Writing to package.json file...")

        packageJsonFile.writeText(
            text = "$text${if(formatting.finalNewline) "\n" else ""}",
            charset = Charsets.UTF_8
        )

        logger.debug("Successfully wrote to package.json file!")
    }

    internal companion object {
        @Internal internal const val DEFAULT_NAME = "packageJson"

        private val pkgJsonProperties by lazy {
            PkgJson::class.memberProperties.asSequence().map { it.name }.toHashSet()
        }
    }
}
