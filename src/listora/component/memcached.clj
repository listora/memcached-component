(ns listora.component.memcached
  (:require [clojurewerkz.spyglass.client :as spyglass]
            [com.stuartsierra.component :as component]))

(defn- silence-spyglass-logger! []
  (System/setProperty "net.spy.log.LoggerImpl"
                      "net.spy.memcached.compat.log.SunLogger")
  (spyglass/set-log-level! :severe))

(def ^:private default-options
  {:auth-type :plain
   :client spyglass/bin-connection
   :conn-type :binary
   :factory spyglass/bin-connection-factory
   :failure-mode :redistribute})

(defn- using-auth?
  [{:keys [username password]}]
  (or username password))

(defn normalize-options
  [config]
  {:pre [(-> config :servers string?)]
   :post [(:auth-type %) (:client %) (:conn-type %) (:factory %)
          (-> % :servers string?)]}
  (let [options (merge default-options config)
        {:keys [username password auth-type conn-type]} options]
    (cond-> options
      (= conn-type :text)
      (assoc :client spyglass/text-connection
             :factory spyglass/text-connection-factory)

      (using-auth? options)
      (assoc :auth-descriptor (spyglass/auth-descriptor username
                                                        password
                                                        auth-type)))))

(defn build-connection
  [{:keys [client factory servers] :as component}]
  (client servers (apply factory component)))

(defrecord MemcachedClient [servers username password failure-mode]
  component/Lifecycle
  (start [component]
    (silence-spyglass-logger!)
    (if (:conn component)
      component
      (assoc component :conn (build-connection component))))
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
    :conn-type    - :text or :binary. Defaults to :binary
    :failure-mode - :redistribute, :retry or :cancel"
  [options]
  (-> options
      normalize-options
      map->MemcachedClient))
