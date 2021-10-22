(ns ecommerce.db
  (:require [datomic.api :as d]))

(def db-uri "datomic:dev://localhost:4334/ecommerce")

(defn open-connection []
  (d/create-database db-uri)
  (d/connect db-uri))

(defn delete-db []
  (d/delete-database db-uri))

(def schema [
             {:db/ident       :product/name
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc         "The name of the product"}
             {:db/ident       :product/slug
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc         "path to access the product in http"}
             {:db/ident       :product/price
              :db/valueType   :db.type/bigdec
              :db/cardinality :db.cardinality/one
              :db/doc         "Price of the product with monetary precision"}
             {:db/ident       :product/tag
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/many
              :db/doc         "Searchable tags for products"}
             ])

(defn create-schema [conn]
  (d/transact conn schema))

(defn all-products [db]
  (d/q '[:find ?entidade
         :where [?entidade :product/name]] db))

; query with dynamic param
(defn all-products-by-slug [db slug]
  (d/q '[:find ?entity
         :in $ ?slug-text                                   ;better use a differente name than parameter to avoid errors
         :where [?entity :product/slug ?slug-text]] db slug)
  )

; query to get other data
(defn all-slugs [db]
  (d/q '[:find ?slug
         ; use _ when the variable is not neede
         :where [_ :product/slug ?slug]] db))

; query to get data from same entity, different rows/columns
; The return is unordered
(defn all-names-prices [db]
  (d/q '[:find ?name ?price
         ; here the product is needed to relate bot searches (?price & ?name)
         :where [?product :product/price ?price]
         [?product :product/name ?name]] db))

; set the desired keys for the map
(defn all-names-prices-map-keys [db]
  (d/q '[:find ?name ?price
         :keys product/name product/price
         :where [?product :product/price ?price]
         [?product :product/name ?name]] db))

; pull to get data related to an entity selecting the fields
(defn all-products-map-selecting [db]
  (d/q '[:find (pull ?entidade [:product/name :product/slug :product/price])
         :where [?entidade :product/name]] db))

; pull to get data related to an entity
(defn all-products-map [db]
  (d/q '[:find (pull ?entidade [*])
         :where [?entidade :product/name]] db))

(defn all-names-prices-minimum [db minimum-price]
  (d/q '[:find ?name ?price
         :in $ ?price-filter
         ; the execution plan is done by the order of the actions
         ; so it is necesary to define the order manually to optimize the query
         :where [?product :product/price ?price]
         [(> ?price ?price-filter)]
         [?product :product/name ?name]] db minimum-price))

(defn all-prducts-by-tag [db tag-to-search]
  (d/q '[:find (pull ?product [*])
         :in $ ?tag
         :where [?product :product/tag ?tag]] db tag-to-search))