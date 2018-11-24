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
package me.kgustave.gradle.pkg.json.internal

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.Serializer
import kotlinx.serialization.json.*
import me.kgustave.gradle.pkg.json.data.Person

@Serializer(forClass = Person::class)
internal object PersonSerializer {
    // internal for testing
    // Credit to https://github.com/jonschlinkert/author-regex for the regex pattern!
    private val parserRegex = Regex("^([^<(]+?)?[ \\t]*(?:<([^>(]+?)>)?[ \\t]*(?:\\(([^)]+?)\\)|\$)")

    override fun serialize(output: Encoder, obj: Person) {
        val (name, email, url, asString) = obj
        if(asString) {
            output.encodeString(buildString {
                append(name)
                if(email != null) append(" <$email>")
                if(url != null) append(" ($url)")
            })
        } else {
            output.beginStructure(descriptor).apply {
                encodeStringElement(descriptor, descriptor.getElementIndex("name"), name)

                if(email != null) {
                    encodeStringElement(descriptor, descriptor.getElementIndex("email"), email)
                }

                if(url != null) {
                    encodeStringElement(descriptor, descriptor.getElementIndex("url"), url)
                }
            }.endStructure(descriptor)
        }
    }

    override fun deserialize(input: Decoder): Person {
        require(input is JSON.JsonInput) { "input must be JsonInput" }

        when(val element = input.readAsTree()) {
            is JsonLiteral -> {
                return parsePersonString(element.content)
            }

            is JsonObject -> {
                val name = element.getOrNull("name")?.contentOrNull
                val email = element.getOrNull("email")?.contentOrNull?.takeIf { it.isNotBlank() }
                val url = element.getOrNull("url")?.contentOrNull?.takeIf { it.isNotBlank() }
                requireNotNull(name) { "'name' field of person object must be set!" }
                require(name.isNotBlank()) { "'name' field of person object must not be blank!" }
                return Person(name, email, url, asString = false)
            }

            else -> throw IllegalArgumentException("Invalid value type! Person should be a string or object!")
        }
    }

    // internal for use in construction function
    internal fun parsePersonString(string: String): Person {
        val match = parserRegex.matchEntire(string)
        requireNotNull(match) { "'$string' is not formatted correctly!" }
        val groups = match.groupValues
        val name = groups[1].takeIf { it.isNotBlank() }
        val email = groups[2].takeIf { it.isNotBlank() }
        val url = groups[3].takeIf { it.isNotBlank() }
        requireNotNull(name) { "'name' value of person string must not be blank!" }
        return Person(name, email, url, asString = true)
    }
}
