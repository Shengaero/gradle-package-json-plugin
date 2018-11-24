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
pluginManagement {
  repositories {
    jcenter()
    maven(url = "https://plugins.gradle.org/m2/")
    maven(url = "https://kotlin.bintray.com/kotlinx")
    maven(url = "${rootProject.projectDir}/build/repository")
  }

  resolutionStrategy {
    eachPlugin {
      val name = when(requested.id.id) {
        "kotlin", "kotlin2js", "kotlin-kapt" -> "kotlin-gradle-plugin"
        "kotlin-allopen" -> "kotlin-allopen"
        "kotlinx-serialization" -> "kotlin-serialization"
        else -> return@eachPlugin
      }

      useModule("org.jetbrains.kotlin:$name:${extra["kotlinVersion"]}")
    }
  }
}

rootProject.name = "gradle-package-json-plugin"

fun sample(name: String) {
  include(":$name")
  project(":$name").projectDir = file("samples/$name")
}

sample("js-library")
sample("kotlin-js-bindings")
