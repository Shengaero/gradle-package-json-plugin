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
import com.moowork.gradle.node.npm.NpmInstallTask
import com.moowork.gradle.node.npm.NpmTask
import com.moowork.gradle.node.task.NodeTask
import me.kgustave.gradle.pkg.json.plugin.PkgJsonTask

plugins {
  id("com.moowork.node") version "1.2.0"
  id("me.kgustave.pkg.json") version "1.0.0"
}

node {
  download = false
  nodeModulesDir = file("node_modules")
}

pkg {
  name = "kotlin-like"
  version = "1.0.0"
  private = true
  main = "src/kotlin-like.js"
  description = "Kotlin functions, but it's actually javascript!"
  keywords = listOf("not-kotlin", "notlin")

  author {
    name = "Kaidan Gustave"
    email = "kaidangustave@yahoo.com"
    url = "kgustave@yahoo.com"
  }

  license = "Apache-2.0"
  scripts = mapOf("test" to "mocha test/tests.js")

  directories {
    lib = "./src"
    test = "./test"
  }
}

dependencies {
  devDependency("mocha:5.2.0")
  devDependency("chai:4.2.0")
}

fun Map<String, String>.dependencyArray(): Array<String> = map { "${it.key}@${it.value}" }.toTypedArray()

tasks.named<PkgJsonTask>("packageJson") {
  group = "node"
  //autoUpdateFile = true
}

task<NpmTask>("installDependencies") {
  group = "npm-dependencies"
  description = "Installs dependencies"
  dependsOn("packageJson")

  setArgs(listOf("install", "--save", *pkg.dependencies.dependencyArray()))
}

task<NpmTask>("installDevDependencies") {
  group = "npm-dependencies"
  description = "Installs dev dependencies"
  dependsOn("packageJson")

  setArgs(listOf("install", "--save-dev", *pkg.devDependencies.dependencyArray()))
}

task<DefaultTask>("npmPackage") {
  group = "node"
  dependsOn("installDependencies")
  dependsOn("installDevDependencies")
}

task<NodeTask>("runMocha") {
  group = "npm-scripts"
  description = "Tests with mocha"

  setScript(file("node_modules/mocha/bin/mocha"))
  setArgs(listOf(file("test/tests.js")))
}

task<Test>("test") {
  group = "verification"
  description = "Runs unit tests"
  dependsOn("runMocha")
}
