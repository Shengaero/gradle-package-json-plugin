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
@file:Suppress("UnstableApiUsage")
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.gradle.ext.ProjectSettings
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties
import java.util.Date

plugins {
  id("idea")
  id("maven-publish")
  id("java-gradle-plugin")
  id("kotlin")
  id("kotlin-kapt")
  id("kotlinx-serialization")
  id("com.jfrog.bintray") version "1.8.4"
  id("org.jetbrains.dokka") version "0.9.17"
  id("org.jetbrains.gradle.plugin.idea-ext") version "0.1"
}

group = "me.kgustave"
version = "1.0.0"

val kotlinVersion: String by ext
val serializationVersion: String by ext
val autoServiceVersion: String by ext
val jupiterVersion: String by ext

repositories {
  jcenter()
  maven(url = "https://kotlin.bintray.com/kotlinx")
  maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
  api(kotlin("stdlib-jdk8", kotlinVersion))
  api(kotlin("gradle-plugin", kotlinVersion))
  api(kotlin("gradle-plugin-api", kotlinVersion))
  api(gradleKotlinDsl())
  api("org.jetbrains.kotlinx:kotlinx-serialization-runtime:$serializationVersion")
  with("com.google.auto.service:auto-service:$autoServiceVersion") {
    api(this)
    kapt(this)
  }

  testImplementation(kotlin("reflect", kotlinVersion))
  testImplementation(kotlin("test-junit5", kotlinVersion))
  testImplementation("org.junit.jupiter:junit-jupiter-api:$jupiterVersion")
  testImplementation("org.junit.jupiter:junit-jupiter-params:$jupiterVersion")
  testImplementation("org.junit.jupiter:junit-jupiter-migrationsupport:$jupiterVersion")
  testRuntime("org.junit.jupiter:junit-jupiter-engine:$jupiterVersion")
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
  sourceSets {
    all {
      with(languageSettings) {
        useExperimentalAnnotation("kotlin.Experimental")
        useExperimentalAnnotation("kotlinx.serialization.ImplicitReflectionSerializer")
      }
    }
  }
}

gradlePlugin {
  plugins {
    register(rootProject.name) {
      id = "me.kgustave.pkg.json"
      implementationClass = "me.kgustave.gradle.pkg.json.plugin.PkgJsonPlugin"
    }
  }
}

idea {
  project {
    require(this is ExtensionAware)
    configure<ProjectSettings> {
      copyright {
        useDefault = "Apache-Standard-License-2.0"
        profiles {
          create("Apache-Standard-License-2.0") {
            keyword = "Copyright"
            notice = file("NOTICE").readText()
          }
        }
      }
    }
  }
}

tasks {
  val clean by "clean"()

  val classes by "classes"()

  val dokka by "dokka"(DokkaTask::class) {
    outputFormat = "html"
    outputDirectory = "$buildDir/javadoc"

    moduleName = rootProject.name
    impliedPlatforms = mutableListOf("JVM")
    includes = listOf("src/main/kotlin/packages.md")

    noStdlibLink = false
    skipDeprecated = true
    skipEmptyPackages = true
    reportUndocumented = false
    includeNonPublic = false
  }

  val jar by "jar"(Jar::class) {
    group = "build"
    description = "Generates a jar"

    baseName = rootProject.name
    version = "${project.version}"
    classifier = ""
  }

  val sourcesJar by register<Jar>("sourcesJar") {
    group = "build"
    description = "Generates a sources jar"

    baseName = rootProject.name
    version = "${project.version}"
    classifier = "sources"

    from(project.sourceSets.main.get().allSource)
  }

  val dokkaJar by register<Jar>("dokkaJar") {
    group = "build"
    description = "Generates a dokka jar"

    dependsOn(dokka)

    baseName = rootProject.name
    version = "${project.version}"
    classifier = "javadoc"

    logging.level = LogLevel.QUIET

    from(dokka.outputDirectory)
  }

  withType<KotlinCompile> {
    incremental = true

    kotlinOptions {
      jvmTarget = "1.8"
      apiVersion = "1.3"
    }
  }

  "test"(Test::class) {
    group = "verification"
    description = "Runs all unit tests."

    useJUnitPlatform()

    // Comment out individual ones to disable test types
    // This is useful when modifying the buildscript and
    //you need to run 'build' often.

//    systemProperty("tests.disable.serialization", "true")
//    systemProperty("tests.disable.plugin", "true")
//    systemProperty("tests.disable.slow", "true")

    testLogging.events(FAILED)
  }

  register<Test>("pluginTests") {
    group = "verification"
    description = "Runs unit tests with the 'plugin-tests' tag."

    useJUnitPlatform {
      includeTags("plugin-tests")
    }
  }

  register<Test>("serializationTests") {
    group = "verification"
    description = "Runs unit tests with the 'serialization-tests' tag."

    useJUnitPlatform {
      includeTags("serialization-tests")
    }
  }

  withType<Test> {
    testLogging {
      displayGranularity = -1
      showStackTraces = true
      showStandardStreams = true
      if(name != "test") {
        events(PASSED, SKIPPED, FAILED)
      }
    }

    dependsOn("cleanTest")
    mustRunAfter("cleanTest")
  }

  val build by "build" {
    dependsOn(clean)
    dependsOn(classes)
    dependsOn(jar)
    dependsOn(sourcesJar)
    dependsOn(dokkaJar)

    classes.mustRunAfter(clean)
    jar.mustRunAfter(classes)
    sourcesJar.mustRunAfter(jar)
    dokkaJar.mustRunAfter(sourcesJar)
  }

  filter { it.group == "publishing" }.forEach { task ->
    task.dependsOn(build)
    task.mustRunAfter(build)
  }

  "wrapper"(Wrapper::class) {
    gradleVersion = "5.0"
  }
}

bintray {
  file("gradle/bintray.properties").takeIf(File::exists)?.reader()?.also { reader ->
    Properties().runCatching { apply { load(reader) } }.onSuccess { properties ->
      user = "${properties["bintray.user.name"]}"
      key = "${properties["bintray.api.key"]}"
      publish = "${properties["bintray.publish"]}".toBoolean()
    }
  }?.close() ?: run { publish = false }

  setPublications("PluginPublish")

  with(pkg) {
    name = rootProject.name
    repo = "maven"
    vcsUrl = "https://github.com/Shengaero/gradle-package-json-plugin.git"
    githubRepo = "https://github.com/Shengaero/gradle-package-json-plugin/"

    setLicenses("Apache-2.0")

    with(version) {
      name = "${project.version}"
      released = "${Date()}"
    }
  }
}

publishing {
  repositories {
    mavenLocal()
    maven(url = "$buildDir/repository") {
      name = "BuildLocal"
    }
  }

  publications {
    create("PluginPublish", MavenPublication::class) {
      this.groupId = "${project.group}"
      this.artifactId = rootProject.name
      this.version = "${project.version}"

      from(project.components["java"])
      artifact(project.tasks["dokkaJar"])
      artifact(project.tasks["sourcesJar"])
    }
  }
}
