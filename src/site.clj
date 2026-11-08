(ns site
  (:require
   [db.atom :as db]))

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

(defn get-frontpage!
  [db]
  (let [books (db/query! db :book db/all)
        authors (db/query! db :author db/all)
        cover-images (db/query! db :cover-image db/all)
        denormalized-books (denormalize books authors cover-images)]
    denormalized-books))

