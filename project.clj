(defproject memcached-component "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :repositories {"spy-memcached" {:url "http://files.couchbase.com/maven2/"}}
  :dependencies [[com.stuartsierra/component "0.2.2"]
                 [clojurewerkz/spyglass "1.1.0" :exclusions [spy/spymemcached]]
                 [org.clojure/clojure "1.6.0"]
                 [spy/spymemcached "2.8.9"]])
