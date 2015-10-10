(ns listora.component.memcached
  (:require [com.stuartsierra.component :as component]
            [clojurewerkz.spyglass.client :as spyglass]))

(defn- silence-spyglass-logger! []
  (System/setProperty "net.spy.log.LoggerImpl" "net.spy.memcached.compat.log.SunLogger")
  (spyglass/set-log-level! :severe))

(defmulti connection-factory identity)
(defmulti connection identity)
(defmethod connection-factory :binary [_] spyglass/bin-connection-factory)
(defmethod connection-factory :text   [_] spyglass/text-connection-factory)
(defmethod connection :binary         [_] spyglass/bin-connection)
(defmethod connection :text           [_] spyglass/text-connection)

(defn- spyglass-connection
  [{:keys [servers username password auth-type failure-mode conn-type]}]
  (let [auth-descrip (if (and username password)
                       (if auth-type
                         (spyglass/auth-descriptor username password auth-type)
                         (spyglass/auth-descriptor username password)))
        conn-factory ((connection-factory conn-type)
                      :auth-descriptor auth-descrip
                      :failure-mode    failure-mode)]
    ((connection conn-type) servers conn-factory)))

(defrecord MemcachedClient [servers username password failure-mode]
  component/Lifecycle
  (start [component]
    (silence-spyglass-logger!)
    (if (:conn component)
      component
      (assoc component :conn (spyglass-connection component))))
  (stop [component]
    (when-let [conn (:conn component)]
      (spyglass/shutdown conn))
    (dissoc component :conn)))

(defn memcached-client
  "Create a component that matches a Memcached connection under the :conn key.

  Accepts the options:
    :servers      - a list of servers in host:port format
    :username     - an optional username for authentication
    :password     - an optional password for authentication
    :auth-type    - :cram-md5 or :plain
    :failure-mode - :redistribute, :retry or :cancel
    :conn-type    - :binary or :text"
  [config]
  (map->MemcachedClient (merge {:failure-mode :redistribute
                                :conn-type    :binary}
                               config)))
