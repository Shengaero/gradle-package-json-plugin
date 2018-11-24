# kotlin-js-bindings

This example module uses the `gradle-package-json-plugin` to assist in
building a functioning set of bindings for a preexisting node.js library.

In the `main` source module, there are bindings for the 
[birb](https://github.com/Purpzie/birb) NPM package.

In the `test` source module, there are some tests to verify
all features of the original package do bind correctly to
their kotlin counterparts.
