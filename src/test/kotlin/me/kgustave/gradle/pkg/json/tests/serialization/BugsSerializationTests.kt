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
import kotlinx.serialization.parse
import kotlinx.serialization.stringify
import me.kgustave.gradle.pkg.json.plugin.internal.data.Bugs
import me.kgustave.gradle.pkg.json.utils.SerializationTests
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import kotlin.test.assertEquals

@SerializationTests
@TestMethodOrder(MethodOrderer.Alphanumeric::class)
class BugsSerializationTests {
    @Test fun `test deserialize bugs with email`() {
        assertEquals(
            expected = Bugs(
                url = "https://github.com/Shengaero/gradle-package-json-plugin/issues",
                email = "kaidangustave@yahoo.com"
            ),
            actual = JSON.parse(
                """{"url":"https://github.com/Shengaero/gradle-package-json-plugin/issues","email":"kaidangustave@yahoo.com"}"""
            )
        )
    }

    @Test fun `test deserialize bugs without email`() {
        assertEquals(
            expected = Bugs(url = "https://github.com/Shengaero/gradle-package-json-plugin/issues"),
            actual = JSON.parse("""{"url":"https://github.com/Shengaero/gradle-package-json-plugin/issues"}""")
        )
    }

    @Test fun `test serialize bugs with email`() {
        assertEquals(
            expected = """{"url":"https://github.com/Shengaero/gradle-package-json-plugin/issues","email":"kaidangustave@yahoo.com"}""",
            actual = JSON.stringify(Bugs(
                url = "https://github.com/Shengaero/gradle-package-json-plugin/issues",
                email = "kaidangustave@yahoo.com"
            ))
        )
    }

    @Test fun `test serialize bugs without email`() {
        assertEquals(
            expected = """{"url":"https://github.com/Shengaero/gradle-package-json-plugin/issues"}""",
            actual = JSON.stringify(Bugs(
                url = "https://github.com/Shengaero/gradle-package-json-plugin/issues"
            ))
        )
    }
}
