(defproject listora/memcached-component "0.1.2"
  :description "A component for managing a connection to Memcached"
  :url "https://github.com/listora/memcached-component"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[com.stuartsierra/component "0.2.2"]
                 [clojurewerkz/spyglass "1.1.0" :exclusions [spy/spymemcached]]
                 [org.clojure/clojure "1.6.0"]
                 [net.spy/spymemcached "2.11.4"]])
