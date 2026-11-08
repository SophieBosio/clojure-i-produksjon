(ns user
  (:require
   [clj-reload.core :as clj-reload]
   [clojure.java.browse :as browse]
   [server])
  (:import
   (java.io File)
   (javax.imageio ImageIO)))

(defn browse!
  [port]
  (browse/browse-url (str "http://localhost:" port)))

(defn go!
  []
  (let [port 8088]
    (when-not @server/server
      (server/start! port))
    (browse! port)))

(defn refresh!
  []
  (server/stop!)
  (clj-reload/reload)
  (server/start! 8088))

(comment
  (go!)
  (refresh!)

  (set! *print-namespace-maps* false)
  ((requiring-resolve 'clojure.repl.deps/sync-deps))

  (let [file (File. "resources/sicp-cover.jpg")
      buffered-image (ImageIO/read file)]
  buffered-image)
  :-)
