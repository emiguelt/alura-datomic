(ns ecommerce.aula03-1
  (:require [clojure.pprint :refer :all]
            [datomic.api :as d]
            [ecommerce.db :as db]
            [ecommerce.model :as model]
            [schema.core :as s]))

(s/set-fn-validation! true)
(db/delete-db!)

(def conn (db/open-connection))
(db/create-schema! conn)

; validate schema
(pprint (s/validate model/Category (model/new-category "Shoes")))
(pprint (s/validate model/Product (model/new-product "tenis" "/tenis" 10.1M)))


(db/create-samples conn)

(pprint (db/all-categories (d/db conn)))


(pprint (db/all-products-with-categories-embedded (d/db conn)))