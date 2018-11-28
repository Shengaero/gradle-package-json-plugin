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
@file:Suppress("MemberVisibilityCanBePrivate")
package me.kgustave.gradle.pkg.json.plugin.conventions

import me.kgustave.gradle.pkg.json.plugin.internal.data.Person
import org.gradle.api.tasks.Internal

/**
 * Configuration for a package.json field that takes a "person" as an argument.
 *
 * In json form, this is an object with three keys:
 *
 * 1) [name][PersonConvention.name] - The name of the person.
 * 2) [email][PersonConvention.email] - The person's email.
 * 3) [url][PersonConvention.url] - The person's url (presumably to a website).
 *
 * Only the name must be specified, all other fields are optional, but note that if the name is
 * not specified mutations to instances of this class have no effect.
 *
 * This convention delegates back to the owning [package-json-convention][PkgJsonConvention].
 */
open class PersonConvention {
    @get:Internal internal var wasModified = false
        private set

    /**
     * The name of the person.
     */
    var name: String? = null
        set(value) {
            field = requireNotNull(value) { "cannot set name to null" }
            wasModified = true
        }

    /**
     * The person's email.
     */
    var email: String? = null
        set(value) {
            field = value
            wasModified = true
        }

    /**
     * The person's url (presumably to a website).
     */
    var url: String? = null
        set(value) {
            field = value
            wasModified = true
        }

    @Internal internal fun buildPerson(): Person? {
        if(!wasModified) return null
        val name = requireNotNull(name) { "Name must be set!" }
        return Person(name, email, url)
    }
}
