(defproject listora/memcached-component "0.1.1"
  :description "A component for managing a connection to Memcached"
  :url "https://github.com/listora/memcached-component"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :repositories {"spy-memcached" {:url "http://files.couchbase.com/maven2/"}}
  :dependencies [[com.stuartsierra/component "0.2.2"]
                 [clojurewerkz/spyglass "1.1.0" :exclusions [spy/spymemcached]]
                 [org.clojure/clojure "1.6.0"]
                 [spy/spymemcached "2.8.9"]])
