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

import me.kgustave.gradle.pkg.json.plugin.internal.data.Repository
import org.gradle.api.tasks.Internal

open class RepositoryConvention {
    @get:Internal internal var wasModified = false
        private set

    var url: String? = null
        set(value) {
            field = requireNotNull(value) { "cannot set url to null" }
            wasModified = true
        }

    var type: String? = null
        set(value) {
            field = requireNotNull(value) { "cannot set type to null" }
            wasModified = true
        }

    @Internal internal fun buildRepository(): Repository? {
        if(!wasModified) return null
        val type = requireNotNull(type) { "type must be specified!" }
        val url = requireNotNull(url) { "url must be specified!" }
        return Repository(type, url)
    }
}
