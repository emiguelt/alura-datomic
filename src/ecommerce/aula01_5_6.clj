(ns ecommerce.aula01_5_6
  (:use clojure.pprint)
  (:require [ecommerce.db :as db]
            [ecommerce.model :as model]
            [datomic.api :as d]))

(def conn (db/open-connection))
(db/create-schema conn)

; moment 1 of db (empty)
(let [computer (model/new-product "Novo computador" "/novo_computador" 4000.5M)
      cellphone (model/new-product "celular" "/cellphone" 400.5M)]
  (pprint @(d/transact conn [computer cellphone])))

; moment 2 of db (2 products)

(let [calculator (model/new-product "Calculadora" "/caluladora" 400.6M)
      lowprice-cellphone (model/new-product "celular barato" "/celular-barato" 10.5M)]
  (pprint @(d/transact conn [calculator lowprice-cellphone])))

; moment 3 of db (4 products)

; print latest
(pprint (db/all-products-map (d/db conn)))

; print second: #inst takes the instant when the transaction was executed
(pprint (db/all-products-map (d/as-of (d/db conn) #inst "2021-10-21T23:56:42.974-00:00")))

; aula 6
(pprint (db/all-names-prices-minimum (d/db conn) 10))
(pprint (db/all-names-prices-minimum (d/db conn) 4000))

(d/transact conn [[:db/add 17592186045418 :product/tag "computer"]
                  [:db/add 17592186045419 :product/tag "cellphone"]
                  [:db/add 17592186045422 :product/tag "cellphone"]])

(pprint (db/all-prducts-by-tag (d/db conn) "computer"))
(pprint (db/all-prducts-by-tag (d/db conn) "cellphone"))

(db/delete-db)
