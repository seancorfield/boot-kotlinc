# boot-kotlinc

A Boot task that provides compilation of Kotlin source files.

[![Clojars Project](https://img.shields.io/clojars/v/seancorfield/boot-kotlinc.svg)](https://clojars.org/seancorfield/boot-kotlinc)

## Basic Usage

Create a Kotlin program in `src/kt`:
```kotlin
fun main(args: Array<String>) {
	println("Hello World!")
}
```

Now you can compile it and access the compiled class from the REPL:

    > boot -d seancorfield/boot-kotlinc kotlinc repl
    ...
    boot.user> (import HelloKt)
    HelloKt
    boot.user> (HelloKt/main (into-array String []))
    Hello World!
    nil
    boot.user>

Or you can compile it and make an uberjar:

    > boot -d seancorfield/boot-kotlinc kotlinc uber jar -m HelloKt target
    Adding uberjar entries...
    Writing project.jar...
    Writing target dir(s)...
    > java -jar target/project.jar
    Hello World!

## Options

You can specify `-v` for verbose mode. This will print out how `boot-kotlinc` is invoking the Kotlin compiler (showing the classpath and other options), as well as telling the Kotlin compiler itself to be verbose.

You can override the default location for Kotlin source code (`src/kt`) with the `-k` or `--source` option. You can specify this multiple times for multiple source locations.

## Roadmap

* Show how to use `boot-kotlinc` in your own `build.boot` file, how to write mixed Clojure/Kotlin projects, etc.
* Add more options (e.g., specifying Kotlin compiler version, ability to compile to `.class` files in a specific output directory).

## License

Copyright © 2017 Sean Corfield.

Distributed under the Eclipse Public License version 1.0.
