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
import me.kgustave.gradle.pkg.json.plugin.PkgJsonTask

plugins {
    id("me.kgustave.pkg.json") version "1.0.0"
}

tasks.withType<PkgJsonTask> {
    cache {
        enabled = true
    }
    pkg {
        name = "simple-build"
        version = "0.0.1"
        license = "Apache-2.0"
        tags = listOf("simple", "gradle", "package-json")

        author {
            name = "Kaidan Gustave"
            url = "https://kgustave.me/"
        }

        dependencies = mapOf(
            "chalk" to "2.4.1",
            "request" to "2.88.0"
        )

        devDependencies = mapOf(
            "mocha" to "5.2.0"
        )
    }
}
