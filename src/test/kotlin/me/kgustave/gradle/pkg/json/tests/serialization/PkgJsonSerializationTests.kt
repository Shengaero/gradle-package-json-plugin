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
import me.kgustave.gradle.pkg.json.data.Person
import me.kgustave.gradle.pkg.json.data.PkgJson
import me.kgustave.gradle.pkg.json.data.Repository
import me.kgustave.gradle.pkg.json.internal.PkgJsonSerializer
import me.kgustave.gradle.pkg.json.utils.SerializationTests
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import kotlin.test.assertEquals

@SerializationTests
@TestMethodOrder(MethodOrderer.Alphanumeric::class)
class PkgJsonSerializationTests {
    // Used primarily for copying and easier/streamlined testing
    private val testPkgJson = PkgJson(name = "test-project", version = "1.0")

    @Test fun `test serialize package json name & version`() {
        assertEquals(
            expected = """{"name":"test-project","version":"1.0"}""",
            actual = JSON.stringify(PkgJsonSerializer, testPkgJson)
        )
    }

    @Test fun `test serialize package json private`() {
        assertEquals(
            expected = """{"name":"test-project","version":"1.0","private":true}""",
            actual = JSON.stringify(PkgJsonSerializer, testPkgJson.copy(private = true))
        )
    }

    @Test fun `test serialize package json description`() {
        assertEquals(
            expected = """{"name":"test-project","version":"1.0","description":"A test package."}""",
            actual = JSON.stringify(PkgJsonSerializer, testPkgJson.copy(
                description = "A test package."
            ))
        )
    }

    @Test fun `test serialize package json main`() {
        assertEquals(
            expected = """{"name":"test-project","version":"1.0","main":"index.js"}""",
            actual = JSON.stringify(PkgJsonSerializer, testPkgJson.copy(
                main = "index.js"
            ))
        )
    }

    @Test fun `test serialize package json author`() {
        assertEquals(
            expected = """{"name":"test-project","version":"1.0",""" +
                """"author":{"name":"John Smith","email":"john@jsmith.me","url":"https://jsmith.me/"}}""",
            actual = JSON.stringify(PkgJsonSerializer, testPkgJson.copy(
                author = Person(
                    name = "John Smith",
                    email = "john@jsmith.me",
                    url = "https://jsmith.me/")
            ))
        )
    }

    @Test fun `test serialize package json author (as string)`() {
        assertEquals(
            expected = """
                {"name":"test-project","version":"1.0","author":"John Smith <john@jsmith.me> (https://jsmith.me/)"}
            """.trimIndent(),
            actual = JSON.stringify(PkgJsonSerializer, testPkgJson.copy(
                author = Person(
                    name = "John Smith",
                    email = "john@jsmith.me",
                    url = "https://jsmith.me/",
                    asString = true
                )
            ))
        )
    }

    @Test fun `test serialize package json tags`() {
        assertEquals(
            expected = """{"name":"test-project","version":"1.0","tags":["foo","bar","baz"]}""",
            actual = JSON.stringify(PkgJsonSerializer, testPkgJson.copy(
                tags = listOf("foo", "bar", "baz")
            ))
        )
    }

    @Test fun `test serialize package json license`() {
        assertEquals(
            expected = """{"name":"test-project","version":"1.0","license":"Apache-2.0"}""",
            actual = JSON.stringify(PkgJsonSerializer, testPkgJson.copy(
                license = "Apache-2.0"
            ))
        )
    }

    @Test fun `test serialize package json licenses`() {
        assertEquals(
            expected = """{"name":"test-project","version":"1.0","licenses":["ISC","Apache-2.0"]}""",
            actual = JSON.stringify(PkgJsonSerializer, testPkgJson.copy(
                licenses = listOf("ISC", "Apache-2.0")
            ))
        )
    }

    @Test fun `test serialize package json license when only 1 element is in licenses list`() {
        assertEquals(
            expected = """{"name":"test-project","version":"1.0","license":"Apache-2.0"}""",
            actual = JSON.stringify(PkgJsonSerializer, testPkgJson.copy(
                licenses = listOf("Apache-2.0")
            ))
        )
    }

    @Test fun `test serialize package json repository`() {
        assertEquals(
            expected = """{"name":"test-project","version":"1.0","repository":""" +
                """{"type":"git","url":"https://github.com/Shengaero/gradle-package-json-plugin.git"}}""",
            actual = JSON.stringify(PkgJsonSerializer, testPkgJson.copy(
                repository = Repository(
                    type = "git",
                    url = "https://github.com/Shengaero/gradle-package-json-plugin.git"
                )
            ))
        )
    }

    @Test fun `test serialize package json dependencies`() {
        assertEquals(
            expected = """{"name":"test-project","version":"1.0","dependencies":{"chalk":"2.4.1"}}""",
            actual = JSON.stringify(PkgJsonSerializer, testPkgJson.copy(
                dependencies = mapOf(
                    "chalk" to "2.4.1"
                )
            ))
        )
    }

    @Test fun `test serialize package json devDependencies`() {
        assertEquals(
            expected = """{"name":"test-project","version":"1.0","devDependencies":{"mocha":"5.2.0"}}""",
            actual = JSON.stringify(PkgJsonSerializer, testPkgJson.copy(
                devDependencies = mapOf(
                    "mocha" to "5.2.0"
                )
            ))
        )
    }

    @Test fun `test serialize package json peerDependencies`() {
        assertEquals(
            expected = """{"name":"test-project","version":"1.0","peerDependencies":{"kotlin":"1.3.10"}}""",
            actual = JSON.stringify(PkgJsonSerializer, testPkgJson.copy(
                peerDependencies = mapOf(
                    "kotlin" to "1.3.10"
                )
            ))
        )
    }
}
