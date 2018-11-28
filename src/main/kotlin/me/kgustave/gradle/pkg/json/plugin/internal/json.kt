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
package me.kgustave.gradle.pkg.json.plugin.internal

import kotlinx.serialization.json.*
import org.gradle.api.tasks.Internal

@Internal internal fun Map<*, *>.toJsonObject(): JsonObject = json {
    for((k, v) in this@toJsonObject) k.toString() to jsonElementOf(v)
}

@Internal internal fun Array<*>.toJsonArray(): JsonArray = jsonArray {
    for(element in this@toJsonArray) +jsonElementOf(element)
}

@Internal internal fun Collection<*>.toJsonArray(): JsonArray = jsonArray {
    for(element in this@toJsonArray) +jsonElementOf(element)
}

@Internal internal fun jsonObjectOf(vararg values: Pair<String, Any?>): JsonObject = json {
    for((k, v) in values) k to jsonElementOf(v)
}

@Internal internal fun jsonArrayOf(vararg values: Any?): JsonArray = jsonArray {
    for(element in values) +jsonElementOf(element)
}

@Internal internal fun jsonElementOf(value: Any?): JsonElement {
    return when(value) {
        null -> JsonNull
        is JsonElement -> value
        is String -> JsonPrimitive(value)
        is Number -> JsonPrimitive(value)
        is Boolean -> JsonPrimitive(value)
        is Array<*> -> value.toJsonArray()
        is Collection<*> -> value.toJsonArray()
        is Map<*, *> -> value.toJsonObject()

        else -> error("Invalid type: ${value::class}")
    }
}

@Internal internal fun JsonElement.stringify(indent: Int = 0): String = buildString {
    when(this@stringify) {
        is JsonObject -> writeObject(this@stringify, indent)
        is JsonArray -> writeArray(this@stringify, indent)
        else -> append("${this@stringify}")
    }
}

private const val QUOTE = '"'
private const val COMMA = ','
private const val COLON = ':'
private const val START_OBJECT = '{'
private const val END_OBJECT   = '}'
private const val START_ARRAY  = '['
private const val END_ARRAY    = ']'

private fun toHexChar(i: Int) : Char {
    val d = i and 0xf
    return if (d < 10) (d + '0'.toInt()).toChar()
    else (d - 10 + 'a'.toInt()).toChar()
}

private val ESCAPE_CHARS: Array<String?> = arrayOfNulls<String>(128).apply {
    for (c in 0..0x1f) {
        val c1 = toHexChar(c shr 12)
        val c2 = toHexChar(c shr 8)
        val c3 = toHexChar(c shr 4)
        val c4 = toHexChar(c)
        this[c] = "\\u$c1$c2$c3$c4"
    }
    this['"'.toInt()] = "\\\""
    this['\\'.toInt()] = "\\\\"
    this['\t'.toInt()] = "\\t"
    this['\b'.toInt()] = "\\b"
    this['\n'.toInt()] = "\\n"
    this['\r'.toInt()] = "\\r"
    this[0x0c] = "\\f"
}

private fun StringBuilder.writeObject(obj: JsonObject, indent: Int, level: Int = 0) {
    append(START_OBJECT)

    var first = true
    val shouldIndent = indent > 0
    val actualIndent = indent * (level + 1)

    for((key, value) in obj) {
        if(!first) append(COMMA) else first = false

        if(shouldIndent) {
            append('\n')
            indent(actualIndent)
        }

        appendQuoted(key)
        append(COLON)
        if(shouldIndent) append(' ')
        when(value) {
            is JsonObject -> writeObject(value, indent, level + 1)
            is JsonArray  -> writeArray(value, indent, level + 1)
            else -> append("$value")
        }
    }

    if(!first && shouldIndent) {
        append('\n')
        indent(actualIndent - indent)
    }

    append(END_OBJECT)
}

private fun StringBuilder.writeArray(array: JsonArray, indent: Int, level: Int = 0) {
    append(START_ARRAY)

    var first = true
    val shouldIndent = indent > 0
    val actualIndent = indent * (level + 1)

    for(value in array) {
        if(!first) {
            append(COMMA)
        } else {
            first = false
        }

        if(shouldIndent) {
            append('\n')
            indent(actualIndent)
        }

        when(value) {
            is JsonObject -> writeObject(value, indent, level + 1)
            is JsonArray  -> writeArray(value, indent, level + 1)
            else -> append("$value")
        }
    }

    if(!first && shouldIndent) {
        append('\n')
        indent(actualIndent - indent)
    }

    append(END_ARRAY)
}

private fun StringBuilder.indent(indent: Int) = repeat(indent) { append(' ') }

private fun StringBuilder.appendQuoted(value: String) {
    append(QUOTE)
    var lastPos = 0
    val length = value.length
    for(i in 0 until length) {
        val c = value[i].toInt()
        if(c >= ESCAPE_CHARS.size) continue
        val esc = ESCAPE_CHARS[c] ?: continue
        append(value, lastPos, i)
        append(esc)
        lastPos = i + 1
    }
    append(value, lastPos, length)
    append(QUOTE)
}
