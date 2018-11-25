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
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinJsDce

plugins {
  id("kotlin2js")
  id("com.moowork.node") version "1.2.0"
  id("me.kgustave.pkg.json") version "1.0.0"
}

apply(plugin = "kotlin-dce-js")

repositories {
  jcenter()
}

dependencies {
  implementation(kotlin("stdlib-js"))

  testImplementation(kotlin("test-js"))
}

kotlin {
  sourceSets {
    all {
      kotlin.srcDir("$name/kotlin")
      resources.srcDir("$name/resources")
    }
  }
}

node {
  download = false
  npmWorkDir = file("$buildDir/npm")
  nodeModulesDir = file("node_modules")
}

tasks {
  // kotlin compilation

  "compileKotlin2Js"(Kotlin2JsCompile::class) {
    kotlinOptions {
      main = "noCall"
      outputFile = file("$destinationDir/birb-kt.js").absolutePath
    }
  }

  "compileTestKotlin2Js"(Kotlin2JsCompile::class) {
    kotlinOptions {
      main = "call"
      outputFile = file("$destinationDir/birb-kt_test.js").absolutePath
    }
  }

  withType<Kotlin2JsCompile> {
    kotlinOptions {
      moduleKind = "commonjs"
      noStdlib = true
      metaInfo = true
      typedArrays = true
      sourceMap = true
      sourceMapEmbedSources = "always"
    }
  }

  // npm installation

  val packageJson by named<PkgJsonTask>("packageJson") {
    group = "node"

    val relativeBuildDir = buildDir.toRelativeString(projectDir).replace(File.separatorChar, '/')

    pkg {
      name = "birb-kt"
      version = "1.2.1"
      private = true
      description = "Kotlin bindings for the birb npm package"
      tags = listOf("kotlin2js", "birb")
      license = "Apache-2.0"
      main = "$relativeBuildDir/kotlin-js-min/main/birb-kt.js"

      scripts = mapOf(
        "test" to "mocha $relativeBuildDir/kotlin-js-min/test/birb-kt_test.js"
      )
      
      author {
        name = "Kaidan Gustave"
        email = "kaidangustave@yahoo.com"
        url = "kgustave@yahoo.com"
      }

      devDependencies = mapOf(
        "mocha" to "5.2.0",
        "kotlin-test" to "1.3.10"
      )

      peerDependencies = mapOf(
        "birb" to "1.2.1",
        "kotlin" to "1.3.10"
      )
    }
  }

  val installDependencies by register<NpmTask>("installDependencies") {
    group = "npm-dependencies"
    description = "Installs dependencies"

    setArgs(listOf("install", "--save",
      *packageJson.pkg.dependencies.dependencyArray()))
  }

  val installDevDependencies by register<NpmTask>("installDevDependencies") {
    group = "npm-dependencies"
    description = "Installs dev dependencies"

    setArgs(listOf("install", "--save-dev",
      *packageJson.pkg.devDependencies.dependencyArray()))
  }

  val installPeerDependencies by register<NpmTask>("installPeerDependencies") {
    group = "npm-dependencies"
    description = "Installs peer dependencies as dev dependencies"

    setArgs(listOf("install", "--save-dev",
      *packageJson.pkg.peerDependencies.dependencyArray()))
  }

  create("npmPackage") {
    group = "node"

    dependsOn(packageJson)
    dependsOn(installDependencies)
    dependsOn(installDevDependencies)
    dependsOn(installPeerDependencies)

    installDependencies.mustRunAfter(packageJson)
    installDevDependencies.mustRunAfter(installDependencies)
    installPeerDependencies.mustRunAfter(installDevDependencies)
  }

  // test configuration

  val runMocha by register<NodeTask>("runMocha") {
    group = "verification"
    description = "Tests with mocha"

    dependsOn("runDceTestKotlinJs")

    setScript(file("node_modules/mocha/bin/mocha"))
    setArgs(listOf(file("$buildDir/kotlin-js-min/test/birb-kt_test.js")))
  }

  "test"(Test::class) {
    dependsOn(runMocha)
  }

  // build order

  "build" {
    dependsOn("clean")

    mustRunAfter("clean")
  }
}

fun Map<String, String>.dependencyArray(): Array<String> =
  map { (dependency, version) -> "$dependency@$version" }.toTypedArray()
