[kotlin2js]: https://kotlinlang.org/docs/reference/js-overview.html
[gradle-node-plugin]: https://github.com/srs/gradle-node-plugin
[sample-using-gradle-node-plugin]: https://github.com/Shengaero/gradle-package-json-plugin/tree/master/samples/kotlin-js-bindings
[kotlinx.serialization]: https://github.com/Kotlin/kotlinx.serialization/

# gradle-package-json-plugin

Manage your project's package.json through gradle!

## Notice!

This project is not officially published yet, and is a work in progress!

## Why?

This project was made as a means of merging gradle's build system with the
package information system implemented by node.js!

It's primary benefit is for users of [kotlin2js][kotlin2js],
as one of the primary struggles of using kotlin2js is organizing build systems. Some noteworthy issues in 
particular are ones regarding automation of package.json information. Updating versions, mapping dependencies,
and other issues exist that require a excessive amount of boilerplate just to make a kotlin2js project build
efficiently.

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

## Managing dependencies with NPM or Yarn

At least right now this shouldn't be confused with a dependency management plugin for node.js.

It's only function is to allow the serialization of information specified in a build.gradle file
into a package.json file.

For using NPM, Yarn, or another node.js dependency manager to download dependencies via gradle
tasks, you'll be better off looking elsewhere for now, however I would highly recommend using
this plugin with the existing [gradle-node-plugin][gradle-node-plugin].

For an example of this, check out the [kotlin-js-bindings][sample-using-gradle-node-plugin] sample project!

## Future Plans

- Cache system and file watchers
- Automatic updating
- Extension support
- Natively supported `node`, `npm`, and `yarn` CLI tasks

## Kotlinx Serialization
Currently, the plugin uses an experimental kotlin serialization library
called [kotlinx.serialization].

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
