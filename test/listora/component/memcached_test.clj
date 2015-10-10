(ns listora.component.memcached-test
  (:import [net.spy.memcached.auth AuthDescriptor])
  (:require [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [clojurewerkz.spyglass.client :as spyglass]
            [listora.component.memcached :refer :all]))

(def ^:const ^:private servers
  "127.0.0.1:11211")

(deftest test-normalize-options
  (testing "defaults"
    (let [options (normalize-options {:servers servers})]
      (is (= (:auth-type options) :plain))
      (is (= (:client options) spyglass/bin-connection))
      (is (= (:conn-type options) :binary))
      (is (= (:factory options) spyglass/bin-connection-factory))
      (is (nil? (:password options)))
      (is (nil? (:username options)))
      (is (nil? (:auth-descriptor options)))))

  (testing "with auth"
    (let [options (normalize-options {:username "user"
                                      :password "pass"
                                      :servers servers})]
      (is (= (:auth-type options) :plain))
      (is (= (:client options) spyglass/bin-connection))
      (is (= (:conn-type options) :binary))
      (is (= (:factory options) spyglass/bin-connection-factory))
      (is (= (:password options) "pass"))
      (is (= (:username options) "user"))
      (is (instance? AuthDescriptor (:auth-descriptor options)))))

  (testing "text connections"
    (let [options (normalize-options {:conn-type :text
                                      :servers servers})]
      (is (= (:auth-type options) :plain))
      (is (= (:client options) spyglass/text-connection))
      (is (= (:conn-type options) :text))
      (is (= (:factory options) spyglass/text-connection-factory))
      (is (nil? (:password options)))
      (is (nil? (:username options)))
      (is (nil? (:auth-descriptor options)))))

  (testing "text connections with auth"
    (let [options (normalize-options {:conn-type :text
                                      :username "user"
                                      :password "pass"
                                      :servers servers})]
      (is (= (:auth-type options) :plain))
      (is (= (:client options) spyglass/text-connection))
      (is (= (:conn-type options) :text))
      (is (= (:factory options) spyglass/text-connection-factory))
      (is (= (:password options) "pass"))
      (is (= (:username options) "user"))
      (is (instance? AuthDescriptor (:auth-descriptor options)))))

  (testing "cram-md5"
    (let [options (normalize-options {:auth-type :cram-md5
                                      :servers servers})]
      (is (= (:auth-type options) :cram-md5)))))

;; These tests assume memcached is started and listening on port 11211

(deftest test-memcached-client
  (let [component (memcached-client {:servers servers})]
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
