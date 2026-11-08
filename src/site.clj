(ns site
  (:require
   [clojure.java.io :as io]
   [db.interface :as db]
   [hiccup2.core :as hiccup]
   [render]))

(defn index-by
  [key-fn value-fn coll]
  (into {} (map (juxt key-fn value-fn) coll)))

(defn denormalize
  [books authors cover-images]
  (let [id->author-name (index-by :id :name authors)
        id->image-filename (index-by :id :filename cover-images)]
    (->> books
         (map (fn [book]
                (-> book
                    (assoc :authors (map id->author-name (:author-ids book)))
                    (dissoc :author-ids)
                    (assoc :cover-image (id->image-filename (:cover-image-id book)))
                    (dissoc :cover-image-id)))))))

(defn render-html
  [hiccup]
  (str (hiccup/html hiccup)))

(defn get-frontpage!
  [db]
  (let [books (db/query! db :book (db/all db))
        authors (db/query! db :author (db/all db))
        cover-images (db/query! db :cover-image (db/all db))
        denormalized-books (denormalize books authors cover-images)]
    (->> denormalized-books
         (render/frontpage)
         render-html)))

(defn get-book!
  [db id]
  (let [book (db/query-one! db :book (db/by db :id id))
        authors (db/query! db :author (db/any-of db :id (:author-ids book)))
        cover-image (db/query-one! db :cover-image (db/by db :id (:cover-image-id book)))
        denormalized-book (first (denormalize [book] authors [cover-image]))]
    (if book
      (render-html (render/book-detail denormalized-book))
      (render-html (render/not-found)))))

(defn get-image!
  [filename]
  (io/resource (str "images/" filename)))

(defn handle-not-found!
  []
  (render-html (render/not-found)))

(defn handle-malformed!
  []
  (render-html (render/malformed)))
