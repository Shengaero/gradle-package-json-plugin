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
@file:Suppress("UnstableApiUsage")

package me.kgustave.gradle.pkg.json.plugin

import com.google.auto.service.AutoService
import me.kgustave.gradle.pkg.json.plugin.conventions.PkgJsonConvention
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.tasks.Internal
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.task

/**
 * The base class for the `gradle-package-json-plugin`.
 */
@AutoService(Plugin::class)
class PkgJsonPlugin: Plugin<Project> {
    private lateinit var pkg: PkgJsonConvention
    private lateinit var task: PkgJsonTask

    override fun apply(target: Project) = with(target) {
        pkg = extensions.create(PkgJsonConvention.NAME, objects)
        task = task<PkgJsonTask>(PkgJsonTask.DEFAULT_NAME)

        val dependency = configurations.create(DEPENDENCY_CONFIGURATION)
        val devDependency = configurations.create(DEV_DEPENDENCY_CONFIGURATION)
        val peerDependency = configurations.create(PEER_DEPENDENCY_CONFIGURATION)
        val bundledDependency = configurations.create(BUNDLED_DEPENDENCY_CONFIGURATION)
        val optionalDependency = configurations.create(OPTIONAL_DEPENDENCY_CONFIGURATION)

        afterEvaluate {
            pkg.dependencies += dependency.dependencies.map { d -> d.rearrange() }
            pkg.devDependencies += devDependency.dependencies.map { d -> d.rearrange() }
            pkg.peerDependencies += peerDependency.dependencies.map { d -> d.rearrange() }
            pkg.bundledDependencies += bundledDependency.dependencies.map { d -> d.rearrange() }
            pkg.optionalDependencies += optionalDependency.dependencies.map { d -> d.rearrange() }
        }

        afterEvaluate {
            if(task.autoUpdateFile) {
                task.generate()
            }
        }
    }

    private fun Dependency.destroy(): Triple<String?, String, String?> = Triple(group, name, version)

    private fun Dependency.rearrange(): Pair<String, String> {
        val (baseGroup, baseName, baseVersion) = destroy()

        @Suppress("LiftReturnOrAssignment")
        if(baseGroup != null && baseGroup.startsWith("@")) {
            requireNotNull(baseVersion) { "Version must be specified for dependency: $baseGroup/$baseName" }
            return "$baseGroup/$baseName" to baseVersion
        } else {
            val name = baseGroup ?: baseName
            val version = baseVersion ?: baseName
            require(name != version) { "Version must be specified for dependency: $name" }
            return name to version
        }
    }

    internal companion object {
        @Internal internal const val ID = "me.kgustave.pkg.json"

        private const val DEPENDENCY_CONFIGURATION = "dependency"
        private const val DEV_DEPENDENCY_CONFIGURATION = "devDependency"
        private const val PEER_DEPENDENCY_CONFIGURATION = "peerDependency"
        private const val BUNDLED_DEPENDENCY_CONFIGURATION = "bundledDependency"
        private const val OPTIONAL_DEPENDENCY_CONFIGURATION = "optionalDependency"
    }
}
