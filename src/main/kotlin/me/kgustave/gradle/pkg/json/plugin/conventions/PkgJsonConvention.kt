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
@file:Suppress("RemoveEmptyPrimaryConstructor", "PropertyName", "unused", "MemberVisibilityCanBePrivate", "UnstableApiUsage")
package me.kgustave.gradle.pkg.json.plugin.conventions

import me.kgustave.gradle.pkg.json.data.Person
import me.kgustave.gradle.pkg.json.data.PkgJson
import me.kgustave.gradle.pkg.json.data.Repository
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Internal
import org.gradle.kotlin.dsl.newInstance
import java.util.LinkedList
import javax.inject.Inject

open class PkgJsonConvention
@Inject constructor(private val factory: ObjectFactory) {
    /////////////////////////
    // GENERAL INFORMATION //
    /////////////////////////

    /**
     * The `name` property for a package.json
     *
     * This is a required field, and must be set, or else the owning
     * [PkgJsonTask][me.kgustave.gradle.pkg.json.plugin.PkgJsonTask] will fail.
     *
     * ### Example
     *
     * In build.gradle:
     * ```groovy
     * name 'my-package'
     * ```
     *
     * Generated package.json:
     * ```json
     * "name": "my-package"
     * ```
     */
    lateinit var name: String

    /**
     * The `version` property for a package.json
     *
     * This is a required field, and must be set, or else the owning
     * [PkgJsonTask][me.kgustave.gradle.pkg.json.plugin.PkgJsonTask] will fail.
     *
     * ### Example
     *
     * In build.gradle:
     * ```groovy
     * version '1.0'
     * ```
     *
     * Generated package.json:
     * ```json
     * "version": "1.0"
     * ```
     */
    lateinit var version: String

    var private: Boolean = false

    var description: String? = null

    var main: String? = null

    var tags = emptyList<String>()
        set(value) { field = value.distinct() }

    //////////////////////
    // REPOSITORY FIELD //
    //////////////////////

    val repository = factory.newInstance<RepositoryConvention>()

    @get:Internal internal val _repository: Repository? get() = with(repository) {
        if(!wasModified) return null
        val type = requireNotNull(type) { "type must be specified!" }
        val url = requireNotNull(url) { "url must be specified!" }
        return Repository(type, url)
    }

    fun repository(action: Action<in RepositoryConvention>) {
        action.execute(repository)
    }

    //////////////////
    // AUTHOR FIELD //
    //////////////////

    /**
     * The author convention used to configure the `author`
     * property for a package.json.
     *
     * @see PersonConvention
     */
    val author = factory.newInstance<PersonConvention>()

    @Internal internal var _author: Person? = null
        get() = field ?: with(author) {
            if (!wasModified) return null
            val name = requireNotNull(name) { "Name must be modified!" }
            return Person(name, email, url)
        }

    fun author(action: Action<in PersonConvention>) {
        action.execute(author)
        if(author.wasModified) {
            _author = null
        }
    }

    fun author(author: String) {
        _author = Person.string(author)
    }

    ////////////////////////
    // CONTRIBUTORS FIELD //
    ////////////////////////

    var contributors: List<PersonConvention.() -> Unit>
        get() = error("getting not supported")
        set(value) {
            _contributors = value.mapNotNull c@ { action ->
                with(factory.newInstance<PersonConvention>()) {
                    action(this)
                    if(!wasModified) return@c null
                    val name = requireNotNull(name) { "Name must be set!" }
                    return@c Person(name, email, url)
                }
            }
        }

    @Internal internal var _contributors = emptyList<Person>()

    fun contributors(contributors: Collection<Action<in PersonConvention>>) {
        this.contributors = contributors.map<Action<in PersonConvention>, PersonConvention.() -> Unit> { action -> { action.execute(this) } }
    }

    ///////////////////////
    // DEPENDENCY FIELDS //
    ///////////////////////

    /**
     * The `dependencies` property for a package.json
     *
     * The values of the map are serialized to the names of
     * dependencies, where their values are the corresponding
     * versions.
     *
     * For configuring development-only dependencies, use
     * the [devDependencies] property.
     *
     * ### Example
     *
     * In build.gradle:
     * ```groovy
     * dependencies (
     *   'chalk': '2.4.1'
     * )
     * ```
     *
     * Generated package.json:
     * ```json
     * "dependencies": {
     *   "chalk": "2.4.1"
     * }
     * ```
     */
    var dependencies = emptyMap<String, String>()

    /**
     * The `devDependencies` property for a package.json.
     *
     * This is configured in a similar manner to the
     * [dependencies] property.
     *
     * ### Example
     *
     * In build.gradle:
     * ```groovy
     * devDependencies (
     *   'mocha': '5.3.0'
     * )
     * ```
     *
     * The following will be produced in the package.json
     * generated by the plugin:
     *
     * ```json
     * "devDependencies": {
     *   "mocha": "5.3.0"
     * }
     * ```
     */
    var devDependencies = emptyMap<String, String>()

    var peerDependencies = emptyMap<String, String>()

    //////////////
    // LICENSES //
    //////////////

    private var _licenses = LinkedList<String>()

    /**
     * The `license` property for a package.json.
     *
     * This delegates to [license] and multiple calls to set
     * this will not overwrite the previous values provided.
     *
     * ### Example
     *
     * In build.gradle:
     * ```groovy
     * license 'Apache-2.0'
     * license 'ISC'
     *
     * assert licenses.size() == 2
     * ```
     *
     * Generation in the package.json is documented
     * in the [licenses] property documentation.
     */
    var license: String?
        get() = _licenses.getOrNull(0)
        set(value) = _licenses.addFirst(value)

    /**
     * The `licenses` property for a package.json.
     *
     * This is a _distinct_ list of license names.
     *
     * **Note**: generation of the property in an output
     * package.json file varies based on the number
     * of license names provided!
     *
     * ### Single License
     *
     * In build.gradle:
     * ```groovy
     * license 'Apache-2.0'
     * ```
     * _or_
     * ```groovy
     * licenses ['Apache-2.0']
     * ```
     *
     * Generated package.json:
     * ```json
     * "license": "Apache-2.0"
     * ```
     *
     * ### Multiple Licenses
     *
     * In build.gradle:
     * ```groovy
     * licenses ['Apache-2.0', 'ISC']
     * ```
     *
     * Generated package.json:
     * ```json
     * "licenses": ['Apache-2.0', 'ISC']
     * ```
     */
    var licenses: List<String>
        get() = _licenses
        set(value) { _licenses = value.asSequence().distinct().toCollection(LinkedList()) }

    /////////////
    // SCRIPTS //
    /////////////

    var scripts = emptyMap<String, String>()

    @Internal internal fun toPkgJson(): PkgJson {
        require(::name.isInitialized) { "package name is required" }
        require(::version.isInitialized) { "package version is required" }

        return PkgJson(
            name = name,
            version = version,
            private = private,
            description = description,
            main = main,
            author = _author,
            contributors = _contributors,
            tags = tags,
            licenses = licenses,
            license = licenses.getOrNull(0),
            scripts = scripts,
            repository = _repository,
            dependencies = dependencies,
            devDependencies = devDependencies,
            peerDependencies = peerDependencies
        )
    }

    internal companion object {
        @Internal internal const val NAME = "pkg"
    }
}
