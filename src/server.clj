(ns server
  (:require
   [db.atom :as atom]
   [org.httpkit.server :as http-kit]
   [ring.middleware.params :as ring]
   [site]))

(defonce server (atom nil))

(defn handler
  [request]
  (let [uri (:uri request)
        db (:db request)]
    (cond
      (= "/" uri)
      {:status 200
       :headers {"Content-Type" "text/html"}
       :body (site/get-frontpage! db)}

      :else
      {:status 404
       :headers {"Content-Type" "text/html"}
       :body "<h1>404</h1>"})))

(defn middleware
  [request]
  (-> request
      (assoc :db atom/db)))

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
