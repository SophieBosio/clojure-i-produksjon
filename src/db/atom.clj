(ns db.atom
  (:require
   [data]))

(defonce connection
  (atom {:book data/books
         :author data/authors
         :cover-image data/cover-images}))

(def db
  {:type :atom
   :connection connection})

(defn create!
  [{:keys [connection] :as db} table entity]
  (->> (fn [tables] (update tables table conj entity))
       (swap! connection)))

(defn update!
  [{:keys [connection] :as db} table updated-entity]
  (->> (fn [tables]
         (update tables table #(mapv (fn [entity]
                                       (if (= (:id entity)
                                              (:id updated-entity))
                                         (merge entity updated-entity)
                                         entity))
                                     %)))
       (swap! connection)))

(defn delete!
  [{:keys [connection] :as db} table entity]
  (->> (fn [tables]
         (update tables table #(remove (fn [candidate]
                                         (= (:id candidate)
                                            (:id entity)))
                                       %)))
       (swap! connection)))

(defn query!
  [{:keys [connection] :as db} table query]
  (->> @connection
       table
       (filter query)))

(defn query-one!
  [db table query]
  (first (query! db table query)))

(def all
  (constantly true))

(defn any-of
  [column values]
  (fn [book]
    (let [target (get book column)]
      (if (coll? target)
        (boolean (some (set values) target))
        (contains? (set values) target)))))

(defn by
  [column value]
  (fn [book]
    (= value (get book column))))

(comment
  (query! db :book (by :title "The Little Schemer"))
  (query! db :book all)

  (query! db :book (by :title "The Little Schemer"))

  (create! db :book {:title "Foo"
                     :rating 1
                     :publication-year 2026
                     :status :finished})
  :-)
