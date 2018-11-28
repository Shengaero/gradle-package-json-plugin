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
package me.kgustave.gradle.pkg.json.plugin.conventions

import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.api.tasks.Internal

open class DependenciesConvention: ExtraPropertiesExtension {
    @Internal internal val dependencies = hashMapOf<String, String>()

    override fun has(name: String): Boolean = name in dependencies
    operator fun contains(name: String): Boolean = has(name)
    override operator fun get(name: String): Any? = dependencies[name]
    override operator fun set(name: String, value: Any?) { dependencies[name] = value.toString() }
    override fun getProperties(): Map<String, String> = dependencies

    infix fun String.to(version: String) = set(this, version)
}
