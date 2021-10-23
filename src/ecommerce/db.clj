(ns ecommerce.db
  (:require [datomic.api :as d]))

(def db-uri "datomic:dev://localhost:4334/ecommerce")

(defn open-connection []
  (d/create-database db-uri)
  (d/connect db-uri))

(defn delete-db! []
  (d/delete-database db-uri))

(def schema [
             ;Product
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
             {:db/ident       :product/id
              :db/valueType   :db.type/uuid
              :db/cardinality :db.cardinality/one
              :db/unique      :db.unique/identity}
             {:db/ident       :product/category
              :db/valueType   :db.type/ref
              :db/cardinality :db.cardinality/one
              }
             ;Category
             {:db/ident       :category/id
              :db/valueType   :db.type/uuid
              :db/cardinality :db.cardinality/one
              :db/unique      :db.unique/identity}
             {:db/ident       :category/name
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc         "The name of the category"}

             ;Transaction
             {:db/ident     :tx-data/ip
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one}
             ])

(defn create-schema! [conn]
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

(defn product-by-dbid [db dbid]
  (d/pull db '[*] dbid))

(defn product-by-id [db id]
  (d/pull db '[*] [:product/id id]))


(defn all-categories [db]
  (d/q '[:find (pull ?category [*])
         :where [?category :category/id]] db))


(defn add-products! [conn products]
  (d/transact conn products))

(defn add-categories! [conn categories]
  (d/transact conn categories))

(defn add-category-to-product! [conn product category]
  (d/transact conn [[:db/add [:product/id (:product/id product)]
                     :product/category
                     [:category/id (:category/id category)]]]))

(defn all-product-names-with-category-names [db]
  (d/q '[:find ?product-name ?category-name
         :where [?product :product/name ?product-name]
         [?product :product/category ?product-cat]
         [?product-cat :category/name ?category-name]] db))

; forward navigation
(defn products-with-category-name [db category-name]
  (d/q '[:find (pull ?product [:product/name :product/price {:product/category [:category/name]}])
         :in $ ?cat-name
         :where [?category :category/name ?cat-name]
         [?product :product/category ?category]] db category-name))

; backward navigation
(defn products-by-category-name [db category-name]
  (d/q '[:find (pull ?category [:category/name {:product/_category [:product/name :product/price]}])
         :in $ ?cat-name
         :where [?category :category/name ?cat-name]] db category-name))


; aggregates
(defn min-max-count-price [db]
  (d/q '[:find (min ?price) (max ?price) (count ?price)
         :with ?product
         :where [?product :product/price ?price]] db))

; grouping
(defn min-max-count-price-by-category [db]
  (d/q '[:find ?cat-name (min ?price) (max ?price) (count ?price)
         :with ?product
         :where [?product :product/price ?price]
         [?product :product/category ?category]
         [?category :category/name ?cat-name]] db))


(defn product-with-max-price [db]
  (d/q '[:find (pull ?product [*])
         :where [(q '[:find (max ?price)
                      :where [_ :product/price ?price]] $)
                 [[?price]]]
         [?product :product/price ?price]] db))


(defn products-with-given-ip [db ip]
  (d/q '[:find (pull ?product [*])
         :in $ ?ip-search
         :where [?transaction :tx-data/ip ?ip-search]
         [?product :product/id _ ?transaction]] db ip))