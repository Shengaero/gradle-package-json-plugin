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
package me.kgustave.gradle.pkg.json.plugin.internal.data

import kotlinx.serialization.*
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.json
import me.kgustave.gradle.pkg.json.plugin.internal.toJsonArray
import me.kgustave.gradle.pkg.json.plugin.internal.toJsonObject

@Serializable
internal data class PkgJson(
    val name: String,
    val version: String,
    @Optional val private: Boolean = false,
    @Optional val description: String? = null,
    @Optional val homepage: String? = null,
    @Optional val main: String? = null,
    @Optional val keywords: List<String> = emptyList(),
    @Optional val author: Person? = null,
    @Optional val contributors: List<Person> = emptyList(),
    @Optional val bugs: Bugs? = null,
    @Optional val licenses: List<String> = emptyList(),
    @Optional val license: String? = null,
    @Optional val directories: Directories? = null,
    @Optional val repository: Repository? = null,
    @Optional val dependencies: Map<String, String> = emptyMap(),
    @Optional val devDependencies: Map<String, String> = emptyMap(),
    @Optional val peerDependencies: Map<String, String> = emptyMap(),
    @Optional val bundledDependencies: Map<String, String> = emptyMap(),
    @Optional val optionalDependencies: Map<String, String> = emptyMap(),
    @Optional val scripts: Map<String, String> = emptyMap(),
    @Optional val files: List<String> = emptyList(),
    @Optional val os: List<String> = emptyList(),
    @Optional val cpu: List<String> = emptyList(),
    @Optional val bin: List<String> = emptyList(),
    @Optional val man: List<String> = emptyList(),
    @Transient @Optional val extra: Map<String, JsonElement> = emptyMap()
): JsonAdapter<JsonObject> {
    override fun toJson(): JsonObject = json {
        // name
        "name" to name

        // version
        "version" to version

        // private
        if(private) {
            "private" to private
        }

        // description
        if(!description.isNullOrBlank()) {
            "description" to description
        }

        // homepage
        if(homepage != null) {
            "homepage" to homepage
        }

        // main
        if(main != null) {
            "main" to main
        }

        // keywords
        if(keywords.isNotEmpty()) {
            "keywords" to keywords.toJsonArray()
        }

        // author
        if(author != null) {
            "author" to author.toJson()
        }

        // contributors
        if(contributors.isNotEmpty()) {
            "contributors" to contributors.map { it.toJson() }.toJsonArray()
        }

        // bugs
        if(bugs != null) {
            "bugs" to bugs.toJson()
        }

        when {
            // licenses
            licenses.size > 1 -> {
                "licenses" to licenses.toJsonArray()
            }

            // license
            license != null || licenses.isNotEmpty() -> {
                "license" to (license ?: licenses[0])
            }
        }

        // directories
        if(directories != null) {
            "directories" to directories.toJson()
        }

        // repository
        if(repository != null) {
            "repository" to repository.toJson()
        }

        // dependencies
        if(dependencies.isNotEmpty()) {
            "dependencies" to dependencies.toJsonObject()
        }

        // devDependencies
        if(devDependencies.isNotEmpty()) {
            "devDependencies" to devDependencies.toJsonObject()
        }

        // peerDependencies
        if(peerDependencies.isNotEmpty()) {
            "peerDependencies" to peerDependencies.toJsonObject()
        }

        // bundledDependencies
        if(bundledDependencies.isNotEmpty()) {
            "bundledDependencies" to bundledDependencies.toJsonObject()
        }

        // optionalDependencies
        if(optionalDependencies.isNotEmpty()) {
            "optionalDependencies" to optionalDependencies.toJsonObject()
        }

        // scripts
        if(scripts.isNotEmpty()) {
            "scripts" to scripts.toJsonObject()
        }

        // files
        if(files.isNotEmpty()) {
            "files" to files.toJsonArray()
        }

        // os
        if(os.isNotEmpty()) {
            "os" to os.toJsonArray()
        }

        // cpu
        if(cpu.isNotEmpty()) {
            "cpu" to cpu.toJsonArray()
        }

        // bin
        if(bin.isNotEmpty()) {
            if(bin.size > 1) {
                "bin" to bin.toJsonArray()
            } else {
                "bin" to bin[0]
            }
        }

        // man
        if(man.isNotEmpty()) {
            if(man.size > 1) {
                "man" to man.toJsonArray()
            } else {
                "man" to man[0]
            }
        }

        // extra
        for((key, value) in extra) {
            key to value
        }
    }

    override fun toString(): String = toJsonString()
}
