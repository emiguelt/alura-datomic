(ns ecommerce.aula02-3_4
  (:use clojure.pprint)
  (:require [ecommerce.db :as db]
            [ecommerce.model :as model]
            [datomic.api :as d]))

(def conn (db/open-connection))
(db/create-schema! conn)

(def computers (model/new-category "Computers"))
(def cellphones (model/new-category "Cellphones"))
(pprint (db/add-categories! conn [computers cellphones]))

(pprint (db/all-categories (d/db conn)))

(def computer (model/new-product "Novo computador" "/novo_computador" 4000.5M))
(def cellphone (model/new-product "celular" "/cellphone" 400.5M))
(def calculator (model/new-product "Calculadora" "/caluladora" 400.6M))
(def lowprice-cellphone (model/new-product "celular barato" "/celular-barato" 10.5M))

(db/add-products! conn [computer cellphone calculator lowprice-cellphone])

(pprint (db/all-products-map (d/db conn)))


(db/add-category-to-product! conn cellphone cellphones)
(db/add-category-to-product! conn lowprice-cellphone cellphones)
(db/add-category-to-product! conn computer computers)

(pprint (db/product-by-id (d/db conn) (:product/id cellphone)))
(pprint (db/all-products-map (d/db conn)))

(pprint (db/all-product-names-with-category-names (d/db conn)))
(pprint (db/products-with-category-name (d/db conn) "Computers"))
(pprint (db/products-by-category-name (d/db conn) "Computers"))

;(db/delete-db)
