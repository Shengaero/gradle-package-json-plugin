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
package birb.test

import birb.Birb
import kotlin.js.RegExp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BirbTests {
    @Test fun testBirbEmoji() {
        assertEquals(expected = "🐦", actual = Birb.emoji)
    }

    @Test fun testBirbEmojis() {
        val emojis = arrayOf("🐦", "🐤", "🕊️", "🦆", "🦅", "🐥", "🦉", "🐧", "🐓")
        assertEquals(expected = emojis.size, actual = Birb.emojis.size)
        emojis.withIndex().forEach { (index, emoji) ->
            assertEquals(expected = emoji, actual = Birb.emojis[index])
        }
    }

    @Test fun testBirbOwO() {
        assertEquals(expected = "OvO", actual = Birb.owo)
    }

    @Test fun testBirbUwU() {
        assertEquals(expected = "UvU", actual = Birb.uwu)
    }

    @Test fun testBirbASCII() {
        assertEquals(
            expected = """
                  ._.
                 /'v'\
                (/___\)
                  " "
            """.trimIndent(),
            actual = Birb.ascii
        )
    }

    @Test fun testBirbSound() {
        assertTrue(js("/A+/").unsafeCast<RegExp>().test(Birb.sound))
    }

    @Test fun testBirbRandom() {
        Birb.random().then { console.log(it) }
    }
}
