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
package me.kgustave.gradle.pkg.json.data

import kotlinx.serialization.*
import me.kgustave.gradle.pkg.json.internal.PkgJsonSerializer

@Serializable(with = PkgJsonSerializer::class)
data class PkgJson(
    val name: String,
    val version: String,
    @Optional val private: Boolean = false,
    @Optional val description: String? = null,
    @Optional val main: String? = null,
    @Optional val author: Person? = null,
    @Optional val contributors: List<Person> = emptyList(),
    @Optional val tags: List<String> = emptyList(),
    @Optional val licenses: List<String> = emptyList(),
    @Optional val license: String? = licenses.getOrNull(0),
    @Optional val scripts: Map<String, String> = emptyMap(),
    @Optional val repository: Repository? = null,
    @Optional val dependencies: Map<String, String> = emptyMap(),
    @Optional val devDependencies: Map<String, String> = emptyMap(),
    @Optional val peerDependencies: Map<String, String> = emptyMap()
)
