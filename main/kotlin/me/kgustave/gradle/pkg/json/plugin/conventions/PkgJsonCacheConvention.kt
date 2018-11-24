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
@file:Suppress("RemoveEmptyPrimaryConstructor", "unused")
package me.kgustave.gradle.pkg.json.plugin.conventions

import me.kgustave.gradle.pkg.json.internal.Open
import org.gradle.api.Incubating
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional

@Open
@Incubating
class PkgJsonCacheConvention constructor() {
    /**
     * Directory in which package.json outputs are cached.
     *
     * **Note** - Package-JSON caching is an __experimental__ feature
     * may be removed or have breaking changes made between releases.
     */
    @Incubating
    @field:[Optional Input]
    var enabled: Boolean = false

    /**
     * Directory in which package.json outputs are cached.
     *
     * **Note** - Package-JSON caching is an __experimental__ feature and
     * must be enabled by setting the [enabled] property to `true`.
     */
    @Incubating
    @field:[Optional Input]
    var outputDirectory: String = ".gradle/package-json-cache"

    /**
     * Name of the file in created in [outputDirectory] where package.json
     * output is cached.
     *
     * **Note** - Package-JSON caching is an __experimental__ feature and
     * must be enabled by setting the [enabled] property to `true`.
     */
    @Incubating
    @field:[Optional Input]
    var outputName: String = "package.json.cache"

    internal companion object {
        @Internal internal const val NAME = "cache"
    }
}
