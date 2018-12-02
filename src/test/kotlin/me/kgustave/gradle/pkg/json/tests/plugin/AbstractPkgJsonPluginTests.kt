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
@file:Suppress("MemberVisibilityCanBePrivate")
package me.kgustave.gradle.pkg.json.tests.plugin

import me.kgustave.gradle.pkg.json.utils.PluginTests
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport
import org.junit.rules.TemporaryFolder
import java.io.File

@PluginTests
@EnableRuleMigrationSupport
abstract class AbstractPkgJsonPluginTests(
    protected val buildScriptName: String,
    protected val projectName: String = "test-project"
) {
    @JvmField @field:Rule val testProjectDir = TemporaryFolder()

    protected lateinit var project: Project

    @BeforeEach fun `create project`() {
        this.project = ProjectBuilder()
            .withProjectDir(testProjectDir.root)
            .withName(projectName)
            .build()
    }

    protected open fun gradleBuildScript(script: String): File =
        testProjectDir.newFile(buildScriptName).apply {
            writeText(script.trimIndent())
        }

    protected fun file(name: String): File = project.file(name)
}
