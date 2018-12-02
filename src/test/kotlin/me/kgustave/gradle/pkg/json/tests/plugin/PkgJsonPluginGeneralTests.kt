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

import me.kgustave.gradle.pkg.json.plugin.PkgJsonPlugin
import me.kgustave.gradle.pkg.json.plugin.PkgJsonTask
import me.kgustave.gradle.pkg.json.utils.PluginTests
import org.junit.jupiter.api.Test
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport
import kotlin.test.assertTrue

@PluginTests
@EnableRuleMigrationSupport
class PkgJsonPluginGeneralTests: AbstractPkgJsonPluginTests(buildScriptName = "build.gradle") {
    @Test fun `test plugin applies`() {
        with(project) {
            plugins.apply(PkgJsonPlugin.ID)
            assertTrue(plugins.hasPlugin(PkgJsonPlugin.ID))
            assertTrue(tasks.getByName(PkgJsonTask.DEFAULT_NAME) is PkgJsonTask)
        }
    }
}
