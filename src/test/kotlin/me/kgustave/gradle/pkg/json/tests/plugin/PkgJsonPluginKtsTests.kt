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
package me.kgustave.gradle.pkg.json.tests.plugin

import kotlinx.serialization.json.JSON
import me.kgustave.gradle.pkg.json.internal.PkgJsonSerializer
import me.kgustave.gradle.pkg.json.plugin.PkgJsonPlugin
import me.kgustave.gradle.pkg.json.plugin.PkgJsonTask
import me.kgustave.gradle.pkg.json.utils.PluginTests
import me.kgustave.gradle.pkg.json.utils.gradleRun
import me.kgustave.gradle.pkg.json.utils.runGradleTask
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@PluginTests
@EnableRuleMigrationSupport
class PkgJsonPluginKtsTests: AbstractProjectTests(buildScriptName = "build.gradle.kts") {
    private val json = JSON(
        indent = "  ",
        indented = true,
        strictMode = true,
        encodeDefaults = false
    )

    @Test fun `test plugin applies (kts)`() {
        with(project) {
            plugins.apply(PkgJsonPlugin.ID)
            assertTrue(plugins.hasPlugin(PkgJsonPlugin.ID))
            assertTrue(tasks.getByName(PkgJsonTask.DEFAULT_NAME) is PkgJsonTask)
        }
    }

    @Test fun `test plugin task succeeds (kts)`() {
        gradleBuildScript("""
            import me.kgustave.gradle.pkg.json.plugin.PkgJsonTask

            plugins {
                id("${PkgJsonPlugin.ID}")
            }

            tasks.withType<PkgJsonTask> {
                pkg {
                    name = "test-project"
                    version = "1.0"
                }
            }
        """)

        val task = runGradleTask(
            task = PkgJsonTask.DEFAULT_NAME,
            projectDir = testProjectDir.root,
            usePluginClasspath = true
        )

        assertNotNull(task)
        assertEquals(TaskOutcome.SUCCESS, task.outcome)
    }

    @Test fun `test plugin task creates file (kts)`() {
        gradleBuildScript("""
            import me.kgustave.gradle.pkg.json.plugin.PkgJsonTask

            plugins {
                id("${PkgJsonPlugin.ID}")
            }

            tasks.withType<PkgJsonTask> {
                pkg {
                    name = "test-project"
                    version = "1.0"
                }
            }
        """)

        gradleRun(
            task = PkgJsonTask.DEFAULT_NAME,
            projectDir = testProjectDir.root,
            usePluginClasspath = true
        )

        assertTrue(file("package.json").exists())
    }

    @Test fun `test plugin task writes to file (kts)`() {
        gradleBuildScript("""
            import me.kgustave.gradle.pkg.json.plugin.PkgJsonTask

            plugins {
                id("${PkgJsonPlugin.ID}")
            }

            tasks.withType<PkgJsonTask> {
                pkg {
                    name = "te-st-proj-ect"
                    version = "1.0.2"
                }
            }
        """)

        gradleRun(
            task = PkgJsonTask.DEFAULT_NAME,
            projectDir = testProjectDir.root,
            usePluginClasspath = true
        )

        val pkgJson = json.parse(PkgJsonSerializer, file("package.json").readText())
        assertEquals("te-st-proj-ect", pkgJson.name)
        assertEquals("1.0.2", pkgJson.version)
    }

    @Test fun `test plugin task adds dependencies to file (kts)`() {
        gradleBuildScript("""
            import me.kgustave.gradle.pkg.json.plugin.PkgJsonTask

            plugins {
                id("${PkgJsonPlugin.ID}")
            }

            tasks.withType<PkgJsonTask> {
                pkg {
                    name = "test-project"
                    version = "1.0"
                    dependencies = mapOf("chalk" to "2.4.1")
                }
            }
        """)

        gradleRun(
            task = PkgJsonTask.DEFAULT_NAME,
            projectDir = testProjectDir.root,
            usePluginClasspath = true
        )

        val pkgJson = json.parse(PkgJsonSerializer, file("package.json").readText())
        assertTrue("chalk" in pkgJson.dependencies)
        assertEquals("2.4.1", pkgJson.dependencies["chalk"])
    }

    @Test fun `test plugin task adds devDependencies to file (kts)`() {
        gradleBuildScript("""
            import me.kgustave.gradle.pkg.json.plugin.PkgJsonTask

            plugins {
                id("${PkgJsonPlugin.ID}")
            }

            tasks.withType<PkgJsonTask> {
                pkg {
                    name = "test-project"
                    version = "1.0"
                    devDependencies = mapOf("mocha" to "5.2.0")
                }
            }
        """)

        gradleRun(
            task = PkgJsonTask.DEFAULT_NAME,
            projectDir = testProjectDir.root,
            usePluginClasspath = true
        )

        val pkgJson = json.parse(PkgJsonSerializer, file("package.json").readText())
        assertTrue("mocha" in pkgJson.devDependencies)
        assertEquals("5.2.0", pkgJson.devDependencies["mocha"])
    }
}
