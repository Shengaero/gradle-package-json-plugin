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
package me.kgustave.gradle.pkg.json.plugin.internal.data

import kotlinx.serialization.Optional
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.json
import me.kgustave.gradle.pkg.json.plugin.internal.PersonSerializer

@Serializable(with = PersonSerializer::class)
internal data class Person(
    val name: String,
    @Optional val email: String? = null,
    @Optional val url: String? = null,
    @Optional val asString: Boolean = false
): JsonAdapter<JsonElement> {
    override fun toJson(): JsonElement {
        if(asString) return JsonPrimitive(buildString {
            append(name)
            if(email != null) append(" <$email>")
            if(url != null) append(" ($url)")
        })

        return json {
            "name" to name
            email?.let { "email" to it }
            url?.let { "url" to it }
        }
    }

    override fun toString(): String = toJsonString()

    companion object {
        @JvmStatic fun string(string: String): Person = PersonSerializer.parsePersonString(string)
    }
}
