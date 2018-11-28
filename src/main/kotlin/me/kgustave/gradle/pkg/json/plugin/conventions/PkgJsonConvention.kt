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

import me.kgustave.gradle.pkg.json.plugin.internal.data.*
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Internal
import org.gradle.kotlin.dsl.newInstance
import java.util.LinkedList
import javax.inject.Inject
import kotlin.collections.ArrayList

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

    var homepage: String? = null

    var main: String? = null

    @Deprecated("replaced with keywords", ReplaceWith("keywords"))
    var tags: List<String>
        get() = keywords
        set(value) { keywords = value }

    var keywords = emptyList<String>()
        set(value) { field = value.distinct() }

    ////////////
    // AUTHOR //
    ////////////

    /**
     * The author convention used to configure the `author`
     * property for a package.json.
     *
     * @see PersonConvention
     */
    val author = factory.newInstance<PersonConvention>()

    @Internal internal var _author: Person? = null
        get() = field ?: author.buildPerson()

    fun author(action: Action<in PersonConvention>) {
        action.execute(author)
        if(author.wasModified) {
            _author = null
        }
    }

    fun author(author: String) {
        _author = Person.string(author)
    }

    //////////////////
    // CONTRIBUTORS //
    //////////////////

    var contributors: List<PersonConvention.() -> Unit> = emptyList()

    @get:Internal internal val _contributors: List<Person>
        get() = contributors.mapNotNull { factory.newInstance<PersonConvention>().apply(it).buildPerson() }

    fun contributor(action: Action<in PersonConvention>) {
        this.contributors += { action.execute(this) }
    }

    fun contributors(actions: Collection<Action<in PersonConvention>>) {
        val c = ArrayList<PersonConvention.() -> Unit>(actions.size)
        for(action in actions) {
            c += { action.execute(this) }
        }
        this.contributors += c
    }

    //////////
    // BUGS //
    //////////

    val bugs = factory.newInstance<BugsConvention>()

    @get:Internal internal val _bugs: Bugs?
        get() = bugs.buildBugs()

    fun bugs(action: Action<in BugsConvention>) {
        action.execute(bugs)
    }

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

    /////////////////
    // DIRECTORIES //
    /////////////////

    val directories = factory.newInstance<DirectoriesConvention>()

    @get:Internal internal val _directories: Directories?
        get() = directories.buildDirectories()

    fun directories(action: Action<in DirectoriesConvention>) {
        action.execute(directories)
    }

    ////////////////
    // REPOSITORY //
    ////////////////

    val repository = factory.newInstance<RepositoryConvention>()

    @get:Internal internal val _repository: Repository?
        get() = repository.buildRepository()

    fun repository(action: Action<in RepositoryConvention>) {
        action.execute(repository)
    }

    //////////////////
    // DEPENDENCIES //
    //////////////////

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

    var bundledDependencies = emptyMap<String, String>()

    var optionalDependencies = emptyMap<String, String>()

    fun dependencies(action: Action<in DependenciesConvention>) {
        val convention = factory.newInstance<DependenciesConvention>()
        action.execute(convention)
        this.dependencies = convention.dependencies
    }

    fun devDependencies(action: Action<in DependenciesConvention>) {
        val convention = factory.newInstance<DependenciesConvention>()
        action.execute(convention)
        this.devDependencies = convention.dependencies
    }

    fun peerDependencies(action: Action<in DependenciesConvention>) {
        val convention = factory.newInstance<DependenciesConvention>()
        action.execute(convention)
        this.peerDependencies = convention.dependencies
    }

    fun bundledDependencies(action: Action<in DependenciesConvention>) {
        val convention = factory.newInstance<DependenciesConvention>()
        action.execute(convention)
        this.bundledDependencies = convention.dependencies
    }

    fun optionalDependencies(action: Action<in DependenciesConvention>) {
        val convention = factory.newInstance<DependenciesConvention>()
        action.execute(convention)
        this.optionalDependencies = convention.dependencies
    }

    /////////////
    // SCRIPTS //
    /////////////

    var scripts = emptyMap<String, String>()

    ///////////
    // FILES //
    ///////////

    var files = emptyList<String>()

    ////////
    // OS //
    ////////

    var os = emptyList<String>()

    /////////
    // CPU //
    /////////

    var cpu = emptyList<String>()

    /////////
    // BIN //
    /////////

    var bin = emptyList<String>()

    /////////
    // MAN //
    /////////

    var man = emptyList<String>()

    fun man(man: String) {
        this.man = listOf(man)
    }

    @Internal internal fun toPkgJson(): PkgJson {
        require(::name.isInitialized) { "package name is required" }
        require(::version.isInitialized) { "package version is required" }

        return PkgJson(
            name = name,
            version = version,
            private = private,
            description = description,
            homepage = homepage,
            main = main,
            keywords = keywords,
            author = _author,
            contributors = _contributors,
            bugs = _bugs,
            licenses = licenses,
            license = licenses.getOrNull(0),
            directories = _directories,
            repository = _repository,
            dependencies = dependencies,
            devDependencies = devDependencies,
            peerDependencies = peerDependencies,
            bundledDependencies = bundledDependencies,
            optionalDependencies = optionalDependencies,
            scripts = scripts,
            files = files,
            os = os,
            cpu = cpu,
            bin = bin,
            man = man
        )
    }

    internal companion object {
        @Internal internal const val NAME = "pkg"
    }
}
