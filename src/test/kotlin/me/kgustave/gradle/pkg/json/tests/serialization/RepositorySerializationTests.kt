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
package me.kgustave.gradle.pkg.json.tests.serialization

import kotlinx.serialization.json.JSON
import kotlinx.serialization.stringify
import me.kgustave.gradle.pkg.json.data.Repository
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RepositorySerializationTests {
    @Test fun `test serialize repository`() {
        assertEquals(
            expected = """{"type":"git","url":"https://github.com/Shengaero/gradle-package-json-plugin.git"}""",
            actual = JSON.stringify(Repository(
                type = "git",
                url = "https://github.com/Shengaero/gradle-package-json-plugin.git"
            ))
        )
    }
}
