(ns db.interface 
  (:require
    [db.datomic :as datomic]
    [db.atom :as atom]
    [db.sql :as sql]))

(defn query!
  [db table query]
  (case (:type db)
    :atom (atom/query! db table query)
    :sql (sql/query! db table query)
    :datomic (datomic/query! db table query)
    (throw (IllegalArgumentException. (str "query! - Unknown DB type! " (:type db))))))

(defn query-one!
  [db table query]
  (case (:type db)
    :atom (atom/query-one! db table query)
    :sql (sql/query-one! db table query)
    :datomic (datomic/query-one! db table query)
    (throw (IllegalArgumentException. (str "query-one! - Unknown DB type! " (:type db))))))

(defn create!
  [db table entity]
  (case (:type db)
    :atom (atom/create! db table entity)
    :sql (sql/create! db table entity)
    :datomic (datomic/create! db table entity)
    (throw (IllegalArgumentException. (str "create! - Unknown DB type! " (:type db))))))

(defn update!
  [db table entity]
  (case (:type db)
    :atom (atom/update! db table entity)
    :sql (sql/update! db table entity)
    :datomic (datomic/update! db table entity)
    (throw (IllegalArgumentException. (str "update! - Unknown DB type! " (:type db))))))

(defn delete!
  [db table entity]
  (case (:type db)
    :atom (atom/delete! db table entity)
    :sql (sql/delete! db table entity)
    :datomic (datomic/delete! db table entity)
    (throw (IllegalArgumentException. (str "delete! - Unknown DB type! " (:type db))))))

(defn all
  [db]
  (case (:type db)
    :atom atom/all
    :sql sql/all
    :datomic datomic/all
    (throw (IllegalArgumentException. (str "all - Unknown DB type! " (:type db))))))

(defn by
  [db column value]
  (case (:type db)
    :atom (atom/by column value)
    :sql (sql/by column value)
    :datomic (datomic/by column value)
    (throw (IllegalArgumentException. (str "by - Unknown DB type! " (:type db))))))

(defn any-of
  [db column values]
  (case (:type db)
    :atom (atom/any-of column values)
    :sql (sql/any-of column values)
    :datomic (datomic/any-of column values)
    (throw (IllegalArgumentException. (str "any-of - Unknown DB type! " (:type db))))))
