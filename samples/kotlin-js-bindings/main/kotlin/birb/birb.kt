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
@file:Suppress("unused")
package birb

import kotlin.js.Promise

/**
 * Module for [birb](https://github.com/Purpzie/birb).
 */
@JsModule(import = "birb")
external object Birb {
    /** A bird emoji. */
    val emoji: String

    /** An array of bird emojis. */
    val emojis: Array<String>

    /** A surprised bird face. */
    val owo: String

    /** A content bird face. */
    val uwu: String

    /** A bird in ASCII. */
    val ascii: String

    /** The sound a bird makes, ranging from 1-100 'A' characters. */
    val sound: String

    /**
     * Makes a request to [random.bird.pw](http://random.bird.pw/).
     *
     * Successful result of the promise is a URL string.
     */
    fun random(): Promise<String>
}
