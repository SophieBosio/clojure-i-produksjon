(ns db.datomic
  (:require
   [data]
   [datomic.api :as d]))

(def schema
  [;; Author
   {:db/ident :author/id
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/unique :db.unique/identity}
   {:db/ident :author/name
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}

   ;; Cover image
   {:db/ident :cover-image/id
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/unique :db.unique/identity}
   {:db/ident :cover-image/filename
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}

   ;; Book
   {:db/ident :book/id
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/unique :db.unique/identity}
   {:db/ident :book/title
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :book/publication-year
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one}
   {:db/ident :book/rating
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one}
   {:db/ident :book/status
    :db/valueType :db.type/keyword
    :db/cardinality :db.cardinality/one}
   {:db/ident :book/author-ids
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/many}
   {:db/ident :book/cover-image-id
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}])

(defn ->qualified-column [table column]
  (keyword (name table) (name column)))

(defn- entity->tx [table entity]
  (update-keys entity (partial ->qualified-column table)))

(defn create-db!
  []
  (let [uri (str "datomic:mem://" (random-uuid))
        _ (d/create-database uri)
        connection (d/connect uri)]
    @(d/transact connection schema)
    @(d/transact connection (mapv (partial entity->tx :author) data/authors))
    @(d/transact connection (mapv (partial entity->tx :cover-image) data/cover-images))
    @(d/transact connection (mapv (partial entity->tx :book) data/books))
    connection))

(defonce connection
  (create-db!))

(def db
  {:type :datomic
   :connection connection})

(defn- ->entity
  [db-val eid]
  (when-let [entity (d/entity db-val eid)]
    (update-keys (into {} (d/touch entity)) (comp keyword name))))

(defn create!
  [{:keys [connection] :as db} table entity]
  (let [{:keys [db-after]} @(d/transact connection [(entity->tx table entity)])]
    (->entity db-after [(->qualified-column table :id) (:id entity)])))

(defn update!
  [{:keys [connection] :as db} table entity]
  (let [tx (-> (entity->tx table entity)
               (assoc :db/id [(->qualified-column table :id) (:id entity)]))
        {:keys [db-after]} @(d/transact connection [tx])]
    (->entity db-after [(->qualified-column table :id) (:id entity)])))

(defn delete!
  [{:keys [connection] :as db} table entity]
  (let [{:keys [db-before]} @(d/transact connection [[:db/retractEntity [(->qualified-column table :id) (:id entity)]]])]
    (->entity db-before [(->qualified-column table :id) (:id entity)])))

(defn query!
  [{:keys [connection] :as db} table where-clauses]
  (let [db-val (d/db connection)
        qualified-where-clauses (mapv (fn [clause]
                                        (if (keyword? (get clause 1))
                                          (update clause 1 (partial ->qualified-column table))
                                          clause))
                                      where-clauses)]
    (->> (d/q {:find '[[?e ...]]
               :where qualified-where-clauses}
              db-val)
         (map #(->entity db-val %)))))

(defn query-one!
  [db table query]
  (first (query! db table query)))

(def all
  [['?e :id]])

(defn by
  [column value]
  [['?e column value]])

(defn any-of
  [column values]
  [[(list 'ground (vec values)) '[?v ...]]
   ['?e column '?v]])
