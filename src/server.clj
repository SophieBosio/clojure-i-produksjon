(ns server
  (:require
   [org.httpkit.server :as http-kit]
   [ring.middleware.params :as ring]))

(defonce server (atom nil))

(defn handler
  [request]
  (let [uri (:uri request)]
    (cond
      (= "/" uri)
      {:status 200
       :headers {"Content-Type" "text/html"}
       :body "<h1>It's alive!!</h1>"}

      :else
      {:status 404
       :headers {"Content-Type" "text/html"}
       :body "<h1>404</h1>"})))

(defn middleware
  [request]
  request)

(defn start!
  [port]
  (reset! server
          (http-kit/run-server (ring/wrap-params (comp #'handler #'middleware))
                               {:port port
                                :legacy-return-value? false})))

(defn stop!
  []
  (when-let [running-server @server]
    @(http-kit/server-stop! running-server)
    (reset! server nil)))

