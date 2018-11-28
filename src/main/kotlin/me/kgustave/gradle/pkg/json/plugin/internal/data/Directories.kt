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
import kotlinx.serialization.json.content
import kotlinx.serialization.json.json

@Serializable
internal data class Directories(
    @Optional val lib: String? = null,
    @Optional val bin: String? = null,
    @Optional val man: String? = null,
    @Optional val doc: String? = null,
    @Optional val examples: String? = null,
    @Optional val test: String? = null
): JsonAdapter<JsonObject> {
    override fun toJson(): JsonObject = json {
        lib?.let { "lib" to it }
        bin?.let { "bin" to it }
        man?.let { "man" to it }
        doc?.let { "doc" to it }
        examples?.let { "examples" to it }
        test?.let { "test" to it }
    }

    @Serializer(forClass = Directories::class)
    companion object {
        override fun serialize(output: Encoder, obj: Directories) {
            val out = output.beginStructure(descriptor)
            obj.toJson().asSequence()
                .map { (key, value) -> descriptor.getElementIndex(key) to value.content }
                .forEach { (index, value) -> out.encodeStringElement(descriptor, index, value) }
            out.endStructure(descriptor)
        }
    }
}
