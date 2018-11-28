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

import kotlinx.serialization.Encoder
import kotlinx.serialization.Optional
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.json

// TODO Support only url

@Serializable
internal data class Bugs(val url: String, @Optional val email: String? = null): JsonAdapter<JsonObject> {
    override fun toJson(): JsonObject = json {
        "url" to url
        email?.let { "email" to it }
    }

    override fun toString(): String = toJsonString()

    @Serializer(forClass = Bugs::class)
    companion object {
        override fun serialize(output: Encoder, obj: Bugs) {
            val out = output.beginStructure(descriptor)

            out.encodeStringElement(descriptor, descriptor.getElementIndex("url"), obj.url)

            if(obj.email != null) {
                out.encodeStringElement(descriptor, descriptor.getElementIndex("email"), obj.email)
            }

            out.endStructure(descriptor)
        }
    }
}
