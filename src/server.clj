(ns server
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]
   [db.atom :as atom]
   [db.datomic :as datomic]
   [db.sql :as sql]
   [org.httpkit.server :as http-kit]
   [ring.middleware.params :as ring]
   [site]))

(defonce server (atom nil))

(def extension->content-type
  {"jpg" "image/jpeg"
   "jpeg" "image/jpeg"
   "png" "image/png"})

(defn handler
  [request]
  (let [uri (:uri request)
        db (:db request)]
    (cond
      (= "/" uri)
      {:status 200
       :headers {"Content-Type" "text/html"}
       :body (site/get-frontpage! db)}

      (str/starts-with? uri "/books/")
      (let [[_ _books book-id action] (str/split uri #"/")]
        (cond
          (not (and (string? book-id)
                    (parse-uuid book-id)))
          {:status 422
           :headers {"Content-Type" "text/html"}
           :body (site/handle-malformed!)}

          (nil? action)
          {:status 200
           :headers {"Content-Type" "text/html"}
           :body (site/get-book! db book-id)}

          (= "rate" action)
          (let [rating  (some-> (get-in request [:form-params "rating"])
                                parse-long)]
            {:status 200
             :headers {"Content-Type" "text/html"}
             :body (site/rate-book! db book-id rating)})))

      (str/starts-with? uri "/images/")
      (let [[_ _images filename] (str/split uri #"/")
            extension  (last (str/split filename #"\."))
            content-type (extension->content-type extension)]
        (if content-type
          (let [image (site/get-image! filename)]
            {:status 200
             :headers {"Content-Type" content-type}
             :body (io/input-stream image)})
          {:status 404
           :headers {"Content-Type" "text/html"}
           :body (site/handle-not-found!)}))

      :else
      {:status 404
       :headers {"Content-Type" "text/html"}
       :body (site/handle-not-found!)})))

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
