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

import kotlinx.serialization.json.json
import me.kgustave.gradle.pkg.json.plugin.internal.jsonArrayOf
import me.kgustave.gradle.pkg.json.plugin.internal.stringify
import me.kgustave.gradle.pkg.json.utils.SerializationTests
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import kotlin.test.assertEquals

@SerializationTests
@TestMethodOrder(MethodOrderer.Alphanumeric::class)
class JsonStringifyTests {
    @Test fun `test stringify json object`() {
        assertEquals(
            expected = """{"foo":"bar"}""",
            actual = json { "foo" to "bar" }.stringify()
        )
    }

    @Test fun `test stringify json array`() {
        assertEquals(
            expected = """["a"]""",
            actual = jsonArrayOf("a").stringify()
        )
    }

    @Test fun `test stringify json object with indent`() {
        assertEquals(
            expected = """
                {
                  "foo": "bar"
                }
            """.trimIndent(),
            actual = json { "foo" to "bar" }.stringify(indent = 2)
        )
    }

    @Test fun `test stringify json array with indent`() {
        assertEquals(
            expected = """
                [
                  "a"
                ]
            """.trimIndent(),
            actual = jsonArrayOf("a").stringify(indent = 2)
        )
    }

    @Test fun `test stringify json object with multiple values`() {
        assertEquals(
            expected = """{"foo":"bar","baz":123}""",
            actual = json {
                "foo" to "bar"
                "baz" to 123
            }.stringify()
        )
    }

    @Test fun `test stringify json array with multiple values`() {
        assertEquals(
            expected = """[1,null,"and 2"]""",
            actual = jsonArrayOf(1, null, "and 2").stringify()
        )
    }

    @Test fun `test stringify json object with multiple values with indent`() {
        assertEquals(
            expected = """
                {
                  "foo": "bar",
                  "baz": 123
                }
            """.trimIndent(),
            actual = json {
                "foo" to "bar"
                "baz" to 123
            }.stringify(indent = 2)
        )
    }

    @Test fun `test stringify json array with multiple values with indent`() {
        assertEquals(
            expected = """
                [
                  1,
                  null,
                  "and 2"
                ]
            """.trimIndent(),
            actual = jsonArrayOf(1, null, "and 2").stringify(indent = 2)
        )
    }

    @Test fun `test stringify json object with inner object value`() {
        assertEquals(
            expected = """{"obj":{"inner":true}}""",
            actual = json {
                "obj" to json {
                    "inner" to true
                }
            }.stringify()
        )
    }

    @Test fun `test stringify json array with inner object value`() {
        assertEquals(
            expected = """[{"zero":0},{"one":1},{"two":2}]""",
            actual = jsonArrayOf(json { "zero" to 0 }, json { "one" to 1 }, json { "two" to 2 }).stringify()
        )
    }

    @Test fun `test stringify json object with inner object value with indent`() {
        assertEquals(
            expected = """
                {
                  "obj": {
                    "inner": true
                  }
                }
            """.trimIndent(),
            actual = json { "obj" to json { "inner" to true } }.stringify(indent = 2)
        )
    }

    @Test fun `test stringify json array with inner object value with indent`() {
        assertEquals(
            expected = """
                [
                  {
                    "zero": 0
                  },
                  {
                    "one": 1
                  },
                  {
                    "two": 2
                  }
                ]
            """.trimIndent(),
            actual = jsonArrayOf(json { "zero" to 0 }, json { "one" to 1 }, json { "two" to 2 }).stringify(indent = 2)
        )
    }

    @Test fun `test stringify json object with inner array value`() {
        assertEquals(
            expected = """{"array":[1,2,"three"]}""",
            actual = json { "array" to jsonArrayOf(1, 2, "three") }.stringify()
        )
    }

    @Test fun `test stringify json array with inner array value`() {
        assertEquals(
            expected = """[["inner","array"],["value"]]""",
            actual = jsonArrayOf(jsonArrayOf("inner", "array"), jsonArrayOf("value")).stringify()
        )
    }

    @Test fun `test stringify json object with inner array value with indent`() {
        assertEquals(
            expected = """
                {
                  "array": [
                    1,
                    2,
                    "three"
                  ]
                }
            """.trimIndent(),
            actual = json {
                "array" to jsonArrayOf(1, 2, "three")
            }.stringify(indent = 2)
        )
    }

    @Test fun `test stringify json array with inner array value with indent`() {
        assertEquals(
            expected = """
                [
                  [
                    "inner",
                    "array"
                  ],
                  [
                    "value"
                  ]
                ]
            """.trimIndent(),
            actual = jsonArrayOf(jsonArrayOf("inner", "array"), jsonArrayOf("value")).stringify(indent = 2)
        )
    }
}
