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

import org.gradle.api.tasks.Internal
import kotlin.math.max

open class PkgJsonFormattingConvention {
    var indentFactor = 2
        set(value) { field = max(0, value) }

    var finalNewline = true

    var shouldIndent: Boolean
        get() = indentFactor > 0
        set(value) {
            if(value) {
                if(indentFactor == 0) {
                    indentFactor = 2
                }
            } else {
                indentFactor = 0
            }
        }

    internal companion object {
        @Internal internal const val NAME = "formatting"
    }
}
