(ns ecommerce.aula02-1_2
  (:use clojure.pprint)
  (:require [ecommerce.db :as db]
            [ecommerce.model :as model]
            [datomic.api :as d]))

(def conn (db/open-connection))
(db/create-schema! conn)

(let
  [
   computer (model/new-product "Novo computador" "/novo_computador" 4000.5M)
   cellphone(model/new-product "celular" "/cellphone" 400.5M)
   calculator (model/new-product "Calculadora" "/caluladora" 400.6M)
   lowprice-cellphone (model/new-product "celular barato" "/celular-barato" 10.5M)
   ]
  (d/transact conn [computer cellphone calculator lowprice-cellphone]))

(pprint (db/all-products-map (d/db conn)))

(def first-product-dbid (-> (db/all-products-map (d/db conn))
                            first
                            first
                            :db/id))

(println "First product db id:" first-product-dbid)
(pprint (db/product-by-dbid (d/db conn) first-product-dbid))

(def first-product-id (-> (db/all-products-map (d/db conn))
                            ffirst
                            :product/id))

(println "First product id:" first-product-id)
(pprint (db/product-by-id (d/db conn) first-product-id))

;(db/delete-db)

;aula 2: if al new product is create with an existing ID, the datom is updated, but not created