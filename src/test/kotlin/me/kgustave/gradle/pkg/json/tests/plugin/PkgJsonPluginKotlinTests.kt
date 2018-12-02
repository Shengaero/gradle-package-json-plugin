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

import kotlinx.serialization.json.*
import me.kgustave.gradle.pkg.json.plugin.PkgJsonPlugin
import me.kgustave.gradle.pkg.json.plugin.PkgJsonTask
import me.kgustave.gradle.pkg.json.utils.PluginTests
import me.kgustave.gradle.pkg.json.utils.gradleRun
import me.kgustave.gradle.pkg.json.utils.gradleRunTask
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@PluginTests
@EnableRuleMigrationSupport
class PkgJsonPluginKotlinTests: AbstractPkgJsonPluginTests(buildScriptName = "build.gradle.kts") {
    @Test fun `test plugin task succeeds (kts)`() {
        gradleBuildScript("""
            plugins {
                id("${PkgJsonPlugin.ID}")
            }

            pkg {
                name = "test-project"
                version = "1.0"
            }
        """)

        val task = gradleRunTask(
            task = PkgJsonTask.DEFAULT_NAME,
            projectDir = testProjectDir.root,
            usePluginClasspath = true
        )

        assertNotNull(task)
        assertEquals(TaskOutcome.SUCCESS, task.outcome)
    }

    @Test fun `test plugin task creates file (kts)`() {
        gradleBuildScript("""
            plugins {
                id("${PkgJsonPlugin.ID}")
            }

            pkg {
                name = "test-project"
                version = "1.0"
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
            plugins {
                id("${PkgJsonPlugin.ID}")
            }

            pkg {
                name = "te-st-proj-ect"
                version = "1.0.2"
            }
        """)

        gradleRun(
            task = PkgJsonTask.DEFAULT_NAME,
            projectDir = testProjectDir.root,
            usePluginClasspath = true
        )

        val pkgJson = JsonTreeParser.parse(file("package.json").readText())
        assertEquals("te-st-proj-ect", pkgJson["name"].content)
        assertEquals("1.0.2", pkgJson["version"].content)
    }

    @Test fun `test plugin task adds dependencies to file (kts)`() {
        gradleBuildScript("""
            plugins {
                id("${PkgJsonPlugin.ID}")
            }

            pkg {
                name = "test-project"
                version = "1.0"
            }

            dependencies {
                dependency("chalk:2.4.1")
            }
        """)

        gradleRun(
            task = PkgJsonTask.DEFAULT_NAME,
            projectDir = testProjectDir.root,
            usePluginClasspath = true
        )

        val pkgJson = JsonTreeParser.parse(file("package.json").readText())
        assertTrue("chalk" in pkgJson["dependencies"].jsonObject)
        assertEquals("2.4.1", pkgJson["dependencies"].jsonObject["chalk"].content)
    }

    @Test fun `test plugin task adds devDependencies to file (kts)`() {
        gradleBuildScript("""
            plugins {
                id("${PkgJsonPlugin.ID}")
            }

            pkg {
                name = "test-project"
                version = "1.0"
            }

            dependencies {
                devDependency("mocha:5.2.0")
            }
        """)

        gradleRun(
            task = PkgJsonTask.DEFAULT_NAME,
            projectDir = testProjectDir.root,
            usePluginClasspath = true
        )

        val pkgJson = JsonTreeParser.parse(file("package.json").readText())
        assertTrue("mocha" in pkgJson["devDependencies"].jsonObject)
        assertEquals("5.2.0", pkgJson["devDependencies"].jsonObject["mocha"].content)
    }

    @Test fun `test plugin task with extra properties`() {
        gradleBuildScript("""
            plugins {
                id("${PkgJsonPlugin.ID}")
            }

            pkg {
                name = "test-project"
                version = "1.0"

                ext["extra1"] = true
                ext["extra2"] = "false"
                ext["extra3"] = null
                ext["array"] = listOf(0, 1, "two")
                ext["object"] = mapOf(
                    "a" to 42,
                    "b" to false
                )
            }
        """)

        gradleRun(
            task = PkgJsonTask.DEFAULT_NAME,
            projectDir = testProjectDir.root,
            usePluginClasspath = true
        )

        val pkgJson = JsonTreeParser.parse(file("package.json").readText())
        assertTrue(pkgJson["extra1"].boolean)
        assertEquals("false", pkgJson["extra2"].content)
        assertTrue(pkgJson["extra3"] is JsonNull)
        assertEquals(0, pkgJson["array"].jsonArray[0].int)
        assertEquals(1, pkgJson["array"].jsonArray[1].int)
        assertEquals("two", pkgJson["array"].jsonArray[2].content)
        assertEquals(42, pkgJson["object"].jsonObject["a"].int)
        assertFalse(pkgJson["object"].jsonObject["b"].boolean)
    }
}
