(ns ecommerce.aula02-5_6
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

(db/add-category-to-product! conn cellphone cellphones)
(db/add-category-to-product! conn lowprice-cellphone cellphones)
(db/add-category-to-product! conn computer computers)

(pprint (db/all-products-map (d/db conn)))

;Add a product with embedded category (new category)
(pprint @(db/add-products! conn [{:product/id       (model/uuid)
                                  :product/price    10M
                                  :product/name     "Camiseta"
                                  :product/slug     "/camiseta"
                                  :product/category {:category/id   (model/uuid)
                                                     :category/name "Roupas"}}]))

;Add a product with embedded category (ref)
(pprint @(db/add-products! conn [{:product/id       (model/uuid)
                                  :product/price    10M
                                  :product/name     "laptop amd"
                                  :product/slug     "/laptop_amd"
                                  :product/category [:category/id (:category/id computers)]}]))

(pprint (db/all-products-map (d/db conn)))

(pprint (db/min-max-count-price (d/db conn)))
(pprint (db/min-max-count-price-by-category (d/db conn)))
(pprint (db/product-with-max-price (d/db conn)))


(def computer2 (model/new-product "Other computador" "/novo_computador" 400.5M))
(def tx-data [:db/add  "datomic.tx" :tx-data/ip "1.2.3.4"])

(pprint @(db/add-products! conn [computer2 tx-data]))

(pprint (db/products-with-given-ip (d/db conn) "1.2.3.4"))

;(db/delete-db)
