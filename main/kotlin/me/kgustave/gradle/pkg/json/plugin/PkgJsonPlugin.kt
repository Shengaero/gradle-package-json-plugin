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
@file:Suppress("RemoveEmptyPrimaryConstructor")
package me.kgustave.gradle.pkg.json.plugin

import com.google.auto.service.AutoService
import kotlinx.serialization.json.JSON
import kotlinx.serialization.json.toBooleanStrictOrNull
import me.kgustave.gradle.pkg.json.internal.Open
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Internal

/**
 * The base class for the `gradle-package-json-plugin`.
 */
@Open
@Suppress("unused")
@AutoService(Plugin::class)
class PkgJsonPlugin(): Plugin<Project> {
    private val json = JSON(
        indent = System.getProperty("package.json.indent") ?: "  ",
        indented = System.getProperty("package.json.indented")?.toBooleanStrictOrNull() ?: true,
        strictMode = true,
        encodeDefaults = false
    )

    private lateinit var pkgJsonTask: PkgJsonTask

    override fun apply(target: Project) {
        @Suppress("UnstableApiUsage")
        this.pkgJsonTask =
            target.tasks.create(PkgJsonTask.DEFAULT_NAME, PkgJsonTask::class.java, json)

        target.afterEvaluate {
            if(pkgJsonTask.autoUpdateFile) {
                pkgJsonTask.generate()
            }
        }
    }

    internal companion object {
        @Internal internal const val ID = "me.kgustave.pkg.json"
    }
}
