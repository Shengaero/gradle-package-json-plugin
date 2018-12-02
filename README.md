[kotlin2js]: https://kotlinlang.org/docs/reference/js-overview.html
[gradle-node-plugin]: https://github.com/srs/gradle-node-plugin
[kotlinx.serialization]: https://github.com/Kotlin/kotlinx.serialization
[license]: https://github.com/Shengaero/gradle-package-json-plugin/tree/master/LICENSE
[discord]: https://discord.gg/XCmwxy8
[discord-widget]: https://discordapp.com/api/guilds/301012120613552138/widget.png

[ ![license][license] ][license]

[ ![Discord][discord-widget] ][discord]

# gradle-package-json-plugin

Manage your project's package.json through gradle!

> **Caution!**
>
> This project is not officially published yet, and is a work in progress!
>
> An official release will be made whenever the API is solidified and ready
> to be maintained with backwards compatibility in mind.

## Why?

This project was made as a means of merging gradle's build system with the package information
system implemented by node.js!

It's primary benefit is for users of [kotlin2js][kotlin2js], as one of the primary struggles of
using kotlin2js is organizing build systems. Some noteworthy issues in particular are ones
regarding automation of package.json information. Updating versions, mapping dependencies, and
other issues exist that require a excessive amount of boilerplate just to make a kotlin2js
project build efficiently.

## Installation

The `gradle-package-json-plugin` can be applied to your project
via one of the two gradle plugin DSL available by the gradle API.

```groovy
buildscript {
    repositories {
        jcenter()
        maven { url 'https://kotlin.bintray.com/kotlinx' }
    }
    
    dependencies {
        classpath 'me.kgustave:gradle-package-json-plugin:<PLUGIN_VERSION>'
    }
}

apply plugin: 'me.kgustave.pkg.json'
```

Or if you use the newer DSL:
```groovy
plugins {
    id 'me.kgustave.pkg.json' version '<PLUGIN_VERSION>'
}
```

> Having trouble installing?
>
> See the [Kotlinx Serialization](#Kotlinx-Serialization) section!

## Usage

Great, you installed the plugin! Now how do you use it?

The `gradle-package-json-plugin` provides a single task that allows
full manipulation of the plugin from top to bottom called `packageJson`!

Here's a simple configuration:
```groovy
pkg {
    name 'my-package'
    version '1.0'
}
```

Easy, right?

From here, simply run the `packageJson` task using `gradlew :packageJson` and it will produce a `package.json` file
in the root directory of your project with the following contents:
```json
{
  "name": "my-package",
  "version": "1.0"
}
```

The task has several configurations, which you can learn about in the documentation, but aside from that there's not
much else to explain!

## Dependency Configurations

Application of this plugin allows usage of the native `dependencies` block in a gradle
build script to manage `package.json` dependencies by adding 5 dependency configurations:

- `dependency`: Adds a dependency to the `dependencies` field of a `package.json` file.
- `devDependency`: Adds a dependency to the `devDependencies` field of a `package.json` file.
- `peerDependency`: Adds a dependency to the `peerDependencies` field of a `package.json` file.
- `optionalDependency`: Adds a dependency to the `optionalDependencies` field of a `package.json` file.
- `bundledDependency`: Adds a dependency to the `bundledDependencies` field of a `package.json` file.

In addition, these configurations use a similar dependency notation to the standard `group:name:version`
notation used by gradle dependencies, the only difference being that the `group` is optional and must be
prefixed with `@` if specified!

```groovy
dependencies {
    // specify dependencies in either a compact
    //string notation or via labeled arguments.
    dependency 'react:16.6'
    dependency name: 'react-dom', version: '16.6'
    
    // You may also specify an organization for the dependency to be located at.
    dependency '@jetbrains:kotlin-ext:1.3.10'
    // Note the '@' prefix.
    // This must be present in order to explicitly specify the organization,
    //or else it will result in a build failure.
}
```

This will produce a the following in the generated `package.json` file:

```json
{
  "dependencies": {
    "react": "16.6",
    "react-dom": "16.6",
    "@jetbrains/kotlin-ext": "1.3.10"
  }
}
```

## Extension

The DSL is also fully extendable, and uses groovy's map, list, and overall extension assignment very well!

```groovy
pkg {
    ext.custom = true
    ext.answer = "forty-two"
    ext.array = [0, 1, 'two']
    ext.object = [
        a: 42,
        b: false
    ]
}
```

This will output the following to the generated `package.json` file:

```json
{
  "custom": true,
  "answer": "forty-two",
  "array": [0, 1, "two"],
  "object": {
    "a": 42,
    "b": false
  }
}
```

> **Note**
> 
> Because manually specified properties is not necessary with this plugin, it is not supported.
> If you have a good use case for manual specification, feel free to open a pull request!

## Managing dependencies with NPM or Yarn

At least right now this shouldn't be confused with a dependency management plugin for node.js.

It's only function is to allow the serialization of information specified in a build.gradle file
into a package.json file.

For using NPM, Yarn, or another node.js dependency manager to download dependencies via gradle
tasks, you'll be better off looking elsewhere for now, however I would highly recommend using
this plugin with the existing [gradle-node-plugin][gradle-node-plugin].

For examples of this, check out some of the sample projects!

## Future Plans

- Cache system and file watchers
- Automatic updating
- Natively supported `node`, `npm`, and `yarn` CLI tasks

## Kotlinx Serialization
Currently, the plugin uses an experimental kotlin serialization library called [kotlinx.serialization].

Because kotlinx.serialization is not published on jcenter as of the time of writing
this, you'll need to add it as a classpath repository for your buildscript! This is
accomplished via the following for the older plugin system:

```groovy
buildscript {
    repositories {
        maven { url 'https://kotlin.bintray.com/kotlinx' }
    }
}
```

If you are using the newer plugins block DSL, you may also use the above code, but
it's recommended you place the following code in your `settings.gradle` instead:

```groovy
pluginManagement {
    repositories {
        maven { url 'https://kotlin.bintray.com/kotlinx' }
    }
}
```

## License

`gradle-package-json-plugin` is licensed under the [Apache 2.0 License][license]
