(ns db.sql
  (:require
   [data]
   [honey.sql :as honey]
   [honey.sql.helpers :as hh]
   [next.jdbc :as jdbc]
   [next.jdbc.prepare :as p]
   [clojure.edn :as edn]
   [clojure.set :as set]
   [clojure.string :as str])
  (:import
   (java.sql PreparedStatement)))

(extend-protocol p/SettableParameter
  clojure.lang.Sequential
  (set-parameter [v ^PreparedStatement s ^long i]
    (.setString s i (str "[" (str/join "," (map pr-str v)) "]"))))

(def table->sql-table
  {:book :book
   :author :author
   :cover-image :cover_image})

(def table->clj->sql
  {:book {:id :id
          :title :title
          :author-ids :author_ids
          :publication-year :publication_year
          :rating :rating
          :status :status
          :cover-image-id :cover_image_id}
   :author {:id :id
            :name :name}
   :cover-image {:id :id
                 :filename :filename}})

(def clj->sql
  (apply merge (vals table->clj->sql)))

(defn- entity->row [table entity]
  (let [clj->sql (table->clj->sql table)]
    (-> (select-keys entity (keys clj->sql))
        (set/rename-keys clj->sql)
        (update-vals #(if (keyword? %) (name %) %))
        (cond-> (contains? entity :author-ids)
          (update :author_ids (fn [author-ids] [:jsonb [:lift author-ids]]))))))

(defn- row->entity [table row]
  (let [sql->clj (set/map-invert (table->clj->sql table))]
    (-> row
        (update-keys (comp keyword name))
        (set/rename-keys sql->clj)
        (cond-> (= table :book)
          (update :status #(some-> % keyword)))
        (cond-> (= table :book)
          (update :author-ids #(some-> % edn/read-string))))))

(defn create-db!
  []
  (let [ds (jdbc/get-connection (jdbc/get-datasource {:dbtype "sqlite" :dbname ":memory:"}))]
    (jdbc/execute! ds ["CREATE TABLE author (
                          id TEXT PRIMARY KEY,
                          name TEXT NOT NULL)"])
    (jdbc/execute! ds ["CREATE TABLE cover_image (
                          id TEXT PRIMARY KEY,
                          filename TEXT NOT NULL)"])
    (jdbc/execute! ds ["CREATE TABLE book (
                          id TEXT PRIMARY KEY,
                          title TEXT NOT NULL,
                          author_ids JSONB DEFAULT '[]',
                          publication_year INTEGER,
                          rating INTEGER,
                          status TEXT,
                          cover_image_id TEXT REFERENCES cover_image(id))"])
    ds))

(defonce connection
  (let [ds (create-db!)]
    (doseq [author data/authors]
      (jdbc/execute! ds
        (honey/format (-> (hh/insert-into :author)
                          (hh/values [(entity->row :author author)]))
                      {:quoted true})))
    (doseq [cover-image data/cover-images]
      (jdbc/execute! ds
        (honey/format (-> (hh/insert-into :cover_image)
                          (hh/values [(entity->row :cover-image cover-image)]))
                      {:quoted true})))
    (doseq [book data/books]
      (jdbc/execute! ds
        (honey/format (-> (hh/insert-into :book)
                          (hh/values [(entity->row :book book)]))
                      {:quoted true})))
    ds))

(def db
  {:type :sql
   :connection connection})

(defn create!
  [{:keys [connection] :as db} table entity]
  (let [sql-table (table->sql-table table)
        row (entity->row table entity)
        query (-> (hh/insert-into sql-table)
                  (hh/values [row]))
        formatted-query (honey/format query {:quoted true})]
    (jdbc/execute! connection formatted-query)))

(defn update!
  [{:keys [connection] :as db} table entity]
  (let [sql-table (table->sql-table table)
        row (entity->row table entity)
        query (-> (hh/update sql-table)
                  (hh/set row)
                  (hh/where [:= :id (:id entity)]))
        formatted-query (honey/format query {:quoted true})]
    (jdbc/execute! connection formatted-query)))

(defn delete!
  [{:keys [connection] :as db} table entity]
  (let [sql-table (table->sql-table table)
        formatted-query (-> (hh/delete-from sql-table)
                            (hh/where [:= :id (:id entity)])
                            (honey/format {:quoted true}))]
    (jdbc/execute! connection formatted-query)))

(defn query!
  [{:keys [connection] :as db} table query]
  (let [sql-table (table->sql-table table)
        columns (for [column (vals (table->clj->sql table))]
                  (if (= column :author_ids)
                    [[:json column] column]
                    column))
        formatted-query (-> (apply hh/select columns)
                            (hh/from sql-table)
                            (hh/where query)
                            (honey/format {:quoted true}))]
    (->> (jdbc/execute! connection formatted-query)
         (map (partial row->entity table)))))

(defn query-one!
  [db table query]
  (first (query! db table query)))

(def all
  [:inline true])

(defn by
  [column value]
  (cond
    (nil? value)
    (by column "")

    (number? value)
    (by column (str value))

    :else
    [:= (clj->sql column) (name value)]))

(def ^:private list-columns
  #{:author-ids})

(defn any-of
  [column values]
  (let [values (vec (set (map #(if (keyword? %) (name %) %) values)))]
    (if (list-columns column)
      ;; `json_each` turns the JSON list into rows with a `value` column.
      [:exists {:select [1]
                :from [[[:json_each (clj->sql column)]]]
                :where [:in :value values]}]
      [:in (clj->sql column) values])))

(comment
  (query! db :book all)
  (query-one! db :book all)
  (query! db :book (by :title "The Little Schemer"))
  (query! db :book (by :title nil))
  (query! db :book (by :rating 5))
  (query! db :book (any-of :author-ids ["e8b1089b-7e49-4c14-9905-3beddc7de07f"
                                        "99415308-f44b-452a-8489-ebeec9e52b39"
                                        "fa794f5c-8394-4a9b-9170-9bcf3f75c0f5"]))

  (create! db :book {:id (str (random-uuid))
                     :title "Gödel, Escher, Bach"
                     :publication-year 1979
                     :rating 5
                     :status :reading
                     :cover-image-id nil
                     :author-ids ["x" "y"]})
  :-)
