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
package me.kgustave.gradle.pkg.json.utils

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.BuildTask
import org.gradle.testkit.runner.GradleRunner
import java.io.File

inline fun runner(block: GradleRunner.() -> Unit): BuildResult {
    return GradleRunner.create().apply(block).build()
}

fun gradleRunTask(task: String, projectDir: File, usePluginClasspath: Boolean = true): BuildTask? {
    return runner {
        withProjectDir(projectDir)
        withArguments(task)
        if(usePluginClasspath) {
            withPluginClasspath()
        }
    }.task(":$task")

}

fun gradleRun(task: String, projectDir: File, usePluginClasspath: Boolean = false): BuildResult {
    return runner {
        withProjectDir(projectDir)
        withArguments(task)
        if(usePluginClasspath) {
            withPluginClasspath()
        }
    }
}
