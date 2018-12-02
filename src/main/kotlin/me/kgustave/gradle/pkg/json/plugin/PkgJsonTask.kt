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
@file:Suppress("unused", "UnstableApiUsage", "MemberVisibilityCanBePrivate", "LiftReturnOrAssignment")
package me.kgustave.gradle.pkg.json.plugin

import kotlinx.serialization.json.JsonObject
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
import java.io.File
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

        if(!cache.enabled) {
            doWithoutCacheEnabled(packageJsonFile)
        } else {
            doWithCacheEnabled(packageJsonFile)
        }
    }

    private fun doWithoutCacheEnabled(packageJsonFile: File, text: String = generatePackageJsonText()) {
        logger.debug("Text conversion complete:\n${text.trim()}")
        logger.debug("Writing to package.json file...")

        packageJsonFile.writeText(text = text, charset = Charsets.UTF_8)

        logger.debug("Successfully wrote to package.json file!")
    }

    private fun doWithCacheEnabled(packageJsonFile: File) {
        doWithoutCacheEnabled(packageJsonFile)
//        logger.debug("Caching is enabled...")
//
//        val cacheDirectory = createCacheDirectory()
//        val cachedPackageJsonFile = project.file("$cacheDirectory/${cache.outputName}")
//
//        if(!cachedPackageJsonFile.exists()) {
//            // TODO logging
//            check(cachedPackageJsonFile.createNewFile()) { "Could not create cache file for package.json file!" }
//        }
//
//        val cachedJson = readJsonFrom(cachedPackageJsonFile)
//        val cachedText = generatePackageJsonText(cachedJson)
//        val newJson = getSpecifiedJson()
//        val newText = generatePackageJsonText(newJson)
//
//        // If the cached json has no content, or
//        //the old json does not equal the cached json.
//        println(cachedJson.isEmpty())
//        if(cachedJson.isEmpty() || cachedText != newText) {
//            logger.debug("Changes detected from cache, will modify...")
//            // write to cache
//            cachedPackageJsonFile.writeText(newText, Charsets.UTF_8)
//            // write to file
//            doWithoutCacheEnabled(packageJsonFile, newText)
//        } else {
//            logger.debug("No changes detected from cache, will not modify!")
//        }
    }

    private fun createCacheDirectory(): File {
        val cacheDirectory: File
        if(cache.outputDirectory == PkgJsonCacheConvention.DEFAULT_OUTPUT_DIR) {
            val rootCacheDirectory = project.rootProject.file(cache.outputDirectory)
            if(!rootCacheDirectory.exists()) {
                check(rootCacheDirectory.mkdirs()) { "Failed to create root cache directory: $rootCacheDirectory" }
            }
            cacheDirectory = project.file("$rootCacheDirectory/${project.name}")
        } else {
            cacheDirectory = project.file(cache.outputDirectory)
        }

        if(!cacheDirectory.exists()) {
            check(cacheDirectory.mkdirs()) { "Failed to create cache directory: $cacheDirectory" }
        }

        return cacheDirectory
    }

    private fun getSpecifiedJson(): JsonObject {
        val specifiedJson = project.extensions.getByType<PkgJsonConvention>().toPkgJson()
        return specifiedJson.copy(
            extra = specifiedJson.extra.asSequence()
                .filter { it.key !in pkgJsonProperties }
                .associate { it.toPair() }
        ).toJson()
    }

    private fun generatePackageJsonText(json: JsonObject = getSpecifiedJson()): String {
        return json.stringify(formatting.indentFactor) + if(formatting.finalNewline) "\n" else ""
    }

    internal companion object {
        @Internal internal const val DEFAULT_NAME = "packageJson"

        private val pkgJsonProperties by lazy {
            PkgJson::class.memberProperties.asSequence().map { it.name }.toHashSet()
        }

        private fun readJsonFrom(file: File): JsonObject {
            val text = file.readText(Charsets.UTF_8)
            return runCatching { JsonTreeParser.parse(text) }.getOrDefault(jsonObjectOf())
        }
    }
}
