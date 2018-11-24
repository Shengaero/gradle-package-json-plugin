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

import kotlinx.serialization.*
import me.kgustave.gradle.pkg.json.data.PkgJson

@Serializer(forClass = PkgJson::class)
internal object PkgJsonSerializer {
    private val StringArraySerializer = String.serializer().list
    private val StringStringMapSerializer = (String.serializer() to String.serializer()).map

    override fun serialize(output: Encoder, obj: PkgJson) {
        val out = output.beginStructure(descriptor)

        // name
        out.encodeStringElement(descriptor, descriptor.getElementIndex("name"), obj.name)

        // version
        out.encodeStringElement(descriptor, descriptor.getElementIndex("version"), obj.version)

        // description
        if(!obj.description.isNullOrBlank()) {
            out.encodeStringElement(descriptor, descriptor.getElementIndex("description"), obj.description)
        }

        // private
        if(obj.private) {
            out.encodeBooleanElement(descriptor, descriptor.getElementIndex("private"), true)
        }

        if(obj.main != null) {
            out.encodeStringElement(descriptor, descriptor.getElementIndex("main"), obj.main)
        }

        // author
        if(obj.author != null) {
            out.encodeSerializableElement(
                desc = descriptor,
                index = descriptor.getElementIndex("author"),
                saver = PersonSerializer,
                value = obj.author
            )
        }

        // tags
        if(obj.tags.isNotEmpty()) {
            out.encodeSerializableElement(
                desc = descriptor,
                index = descriptor.getElementIndex("tags"),
                saver = StringArraySerializer,
                value = obj.tags
            )
        }

        // licenses
        when {
            obj.licenses.size > 1 -> out.encodeSerializableElement(
                desc = descriptor,
                index = descriptor.getElementIndex("licenses"),
                saver = StringArraySerializer,
                value = obj.licenses
            )

            obj.license != null || obj.licenses.isNotEmpty() -> out.encodeStringElement(
                desc = descriptor,
                index = descriptor.getElementIndex("license"),
                value = obj.license ?: obj.licenses[0]
            )
        }

        // dependencies
        if(obj.dependencies.isNotEmpty()) {
            out.encodeSerializableElement(
                desc = descriptor,
                index = descriptor.getElementIndex("dependencies"),
                saver = StringStringMapSerializer,
                value = obj.dependencies
            )
        }

        // devDependencies
        if(obj.devDependencies.isNotEmpty()) {
            out.encodeSerializableElement(
                desc = descriptor,
                index = descriptor.getElementIndex("devDependencies"),
                saver = StringStringMapSerializer,
                value = obj.devDependencies
            )
        }

        if(obj.peerDependencies.isNotEmpty()) {
            out.encodeSerializableElement(
                desc = descriptor,
                index = descriptor.getElementIndex("peerDependencies"),
                saver = StringStringMapSerializer,
                value = obj.devDependencies
            )
        }

        out.endStructure(descriptor)
    }
}
