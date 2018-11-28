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
import me.kgustave.gradle.pkg.json.plugin.internal.data.Person
import me.kgustave.gradle.pkg.json.plugin.internal.PersonSerializer
import me.kgustave.gradle.pkg.json.utils.SerializationTests
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import kotlin.test.assertEquals

@SerializationTests
@TestMethodOrder(MethodOrderer.Alphanumeric::class)
class PersonSerializationTests {
    @Test fun `test deserialize person with only name`() {
        assertEquals(
            expected = Person("Kaidan Gustave", asString = false),
            actual = JSON.parse(PersonSerializer, """{"name":"Kaidan Gustave"}""")
        )
    }

    @Test fun `test deserialize person with only name (from string)`() {
        assertEquals(
            expected = Person("Kaidan Gustave", asString = true),
            actual = JSON.parse(PersonSerializer, "\"Kaidan Gustave\"")
        )
    }

    @Test fun `test deserialize person with all properties`() {
        assertEquals(
            expected = Person(
                name = "Barney Rubble",
                email = "b@rubble.com",
                url = "http://barnyrubble.tumblr.com/",
                asString = false
            ),
            actual = JSON.parse(
                serializer = PersonSerializer,
                string = """{"name":"Barney Rubble","email":"b@rubble.com","url":"http://barnyrubble.tumblr.com/"}"""
            )
        )
    }

    @Test fun `test deserialize person with all properties (from string)`() {
        assertEquals(
            expected = Person(
                name = "Barney Rubble",
                email = "b@rubble.com",
                url = "http://barnyrubble.tumblr.com/",
                asString = true
            ),
            actual = JSON.parse(PersonSerializer, "\"Barney Rubble <b@rubble.com> (http://barnyrubble.tumblr.com/)\"")
        )
    }

    @Test fun `test serialize person with only name`() {
        assertEquals(
            expected = """{"name":"Kaidan Gustave"}""",
            actual = JSON.stringify(PersonSerializer, Person("Kaidan Gustave"))
        )
    }

    @Test fun `test serialize person with only name (to string)`() {
        assertEquals(
            expected = "\"Kaidan Gustave\"",
            actual = JSON.stringify(PersonSerializer, Person("Kaidan Gustave", asString = true))
        )
    }

    @Test fun `test serialize person with all properties`() {
        assertEquals(
            expected = """{"name":"Barney Rubble","email":"b@rubble.com","url":"http://barnyrubble.tumblr.com/"}""",
            actual = JSON.stringify(PersonSerializer, Person(
                name = "Barney Rubble",
                email = "b@rubble.com",
                url = "http://barnyrubble.tumblr.com/",
                asString = false
            ))
        )
    }

    @Test fun `test serialize person with all properties (to string)`() {
        assertEquals(
            expected = "\"Barney Rubble <b@rubble.com> (http://barnyrubble.tumblr.com/)\"",
            actual = JSON.stringify(PersonSerializer, Person(
                name = "Barney Rubble",
                email = "b@rubble.com",
                url = "http://barnyrubble.tumblr.com/",
                asString = true
            ))
        )
    }
}
