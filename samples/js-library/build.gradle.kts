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
import org.gradle.plugins.ide.idea.model.Module
import org.gradle.plugins.ide.idea.model.Path
import org.jetbrains.gradle.ext.ModuleSettings
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinJsDce

plugins {
  id("idea")
  id("com.moowork.node") version "1.2.0"
  id("me.kgustave.pkg.json") version "1.0.0"
  id("org.jetbrains.gradle.plugin.idea-ext")
}

node {
  download = false
  npmWorkDir = file("build/npm")
  nodeModulesDir = file("node_modules")
}

fun Map<String, String>.dependencyArray(): Array<String> =
  map { (dependency, version) -> "$dependency@$version" }.toTypedArray()

tasks {
  // npm installation

  val packageJson by named<PkgJsonTask>("packageJson") {
    group = "node"
    pkg {
      name = "kotlin-like"
      version = "1.0.0"
      private = true
      description = "Kotlin functions, but it's actually javascript!"
      tags = listOf("not-kotlin", "notlin")
      license = "Apache-2.0"
      main = "src/kotlin-like.js"

      author {
        name = "Kaidan Gustave"
        email = "kaidangustave@yahoo.com"
        url = "kgustave@yahoo.com"
      }

      devDependencies = mapOf(
        "mocha" to "5.2.0",
        "chai" to "4.2.0"
      )
    }
  }

  val installDependencies by register<NpmTask>("installDependencies") {
    group = "npm-dependencies"
    description = "Installs dependencies"

    setArgs(listOf("install", "--save", *packageJson.pkg.dependencies.dependencyArray()))
  }

  val installDevDependencies by register<NpmTask>("installDevDependencies") {
    group = "npm-dependencies"
    description = "Installs dev dependencies"

    setArgs(listOf("install", "--save-dev", *packageJson.pkg.devDependencies.dependencyArray()))
  }

  create("npmPackage") {
    group = "node"

    dependsOn(packageJson)
    dependsOn(installDependencies)
    dependsOn(installDevDependencies)

    installDependencies.mustRunAfter(packageJson)
    installDevDependencies.mustRunAfter(installDependencies)
  }

  // test configuration

  val runMocha by register<NodeTask>("runMocha") {
    group = "npm-scripts"
    description = "Tests with mocha"

    setScript(file("node_modules/mocha/bin/mocha"))
    setArgs(listOf(file("test/tests.js")))
  }

  create<Test>("test") {
    group = "testing"
    description = "Runs tests"
    dependsOn(runMocha)
  }
}
