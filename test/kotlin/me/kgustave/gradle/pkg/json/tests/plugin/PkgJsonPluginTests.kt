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
class PkgJsonPluginTests: AbstractProjectTests(buildScriptName = "build.gradle") {
    private val json = JSON(
        indent = "  ",
        indented = true,
        strictMode = true,
        encodeDefaults = false
    )

    @Test fun `test plugin applies`() {
        with(project) {
            plugins.apply(PkgJsonPlugin.ID)
            assertTrue(plugins.hasPlugin(PkgJsonPlugin.ID))
            assertTrue(tasks.getByName(PkgJsonTask.DEFAULT_NAME) is PkgJsonTask)
        }
    }

    @Test fun `test plugin task succeeds`() {
        gradleBuildScript("""
            plugins {
                id '${PkgJsonPlugin.ID}'
            }

            packageJson {
                pkg {
                    name 'test-project'
                    version '1.0'
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

    @Test fun `test plugin task creates file`() {
        gradleBuildScript("""
            plugins {
                id '${PkgJsonPlugin.ID}'
            }

            packageJson {
                pkg {
                    name 'test-project'
                    version '1.0'
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

    @Test fun `test plugin task writes to file`() {
        gradleBuildScript("""
            plugins {
                id '${PkgJsonPlugin.ID}'
            }

            packageJson {
                pkg {
                    name 'te-st-proj-ect'
                    version '1.0.2'
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

    @Test fun `test plugin task adds dependencies to file`() {
        gradleBuildScript("""
            plugins {
                id '${PkgJsonPlugin.ID}'
            }

            packageJson {
                pkg {
                    name 'test-project'
                    version '1.0'
                    dependencies ('chalk': '2.4.1')
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

    @Test fun `test plugin task adds devDependencies to file`() {
        gradleBuildScript("""
            plugins {
                id '${PkgJsonPlugin.ID}'
            }

            packageJson {
                pkg {
                    name 'test-project'
                    version '1.0'
                    devDependencies ('mocha': '5.2.0')
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
