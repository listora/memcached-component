(ns listora.component.memcached-test
  (:require [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [clojurewerkz.spyglass.client :as spyglass]
            [listora.component.memcached :refer :all]))

;; These tests assume memcached is started and listening on port 11211

(deftest test-memcached
  (let [component (memcached {:servers "127.0.0.1:11211"})]
    (testing "initial values"
      (is (= (:servers component) "127.0.0.1:11211"))
      (is (nil? (:conn component))))
    (testing "start and stop"
      (let [component (component/start component)]
        (is (:conn component))
        (let [conn (:conn component)]
          (spyglass/set conn "memcached.test" 5 "foobar")
          (is (= (spyglass/get conn "memcached.test") "foobar")))
        (let [component (component/stop component)]
          (is (nil? (:conn component))))))))
