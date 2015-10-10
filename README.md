# Memcached-Component [![Circle CI](https://circleci.com/gh/listora/memcached-component/tree/master.svg?style=svg)](https://circleci.com/gh/listora/memcached-component/tree/master)

A [component][] that holds a [Spymemcached][] client connection,
compatible with [Spyglass][]. Starting the component will create the
connection, while stopping the component will close it.

[component]: https://github.com/stuartsierra/component
[Spymemcached]: https://code.google.com/p/spymemcached/
[Spyglass]: https://github.com/clojurewerkz/spyglass

## Installation

Add the following dependency to your project.clj file:

```clojure
[listora/memcached-component "0.1.2"]
```

## Usage

Require the library:

```clojure
(require '[listora.component.memcached :refer [memcached-client]]
         '[com.stuartsierra.component :as component])
```

Then create the client component:

```clojure
(memcached-client {:servers "127.0.0.1:11211"})
```

Starting the component will create a `:conn` key:

```clojure
(-> (memcached-client {:servers "127.0.0.1:11211"})
    (component/start)
    :conn)
```

This key will be removed when the component is stopped.

## License

Copyright © 2014 Listora

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
