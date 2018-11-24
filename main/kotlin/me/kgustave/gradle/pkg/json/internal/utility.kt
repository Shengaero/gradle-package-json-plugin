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

import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.tasks.Internal
import kotlin.reflect.KProperty

@Internal
internal inline operator fun <reified T: Any> ExtensionContainer.getValue(instance: Any?, property: KProperty<*>): T {
    val ext = checkNotNull(this.findByName(property.name)) { "Could not find ${property.name} in extension container!" }
    return checkNotNull(ext as? T) { "Extension with name ${property.name} was not of type ${T::class.java}" }
}
