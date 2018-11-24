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
package me.kgustave.gradle.pkg.json.utils

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.condition.DisabledIfSystemProperty

@Tag("plugin-tests")
@DisabledIfSystemProperty(named = "tests.disable.plugin", matches = "true")
annotation class PluginTests

@Tag("serialization-tests")
@DisabledIfSystemProperty(named = "tests.disable.serialization", matches = "true")
annotation class SerializationTests

@Tag("slow-test")
@DisabledIfSystemProperty(named = "tests.disable.slow", matches = "true")
annotation class SlowTest
