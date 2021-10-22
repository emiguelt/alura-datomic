(ns ecommerce.aula01_3_4
  (:use clojure.pprint)
  (:require [ecommerce.db :as db]
            [ecommerce.model :as model]
            [datomic.api :as d]))

(def conn (db/open-connection))
(db/create-schema conn)

(let
  [
   computer (model/new-product "Novo computador" "/novo_computador" 4000.5M)
   cellphone(model/new-product "celular" "/cellphone" 400.5M)
   calculator (model/new-product "Calculadora" "/caluladora" 400.6M)
   lowprice-cellphone (model/new-product "celular barato" "/celular-barato" 10.5M)
   ]
  (d/transact conn [computer cellphone calculator lowprice-cellphone]))

(pprint (db/all-products (d/db conn)))
(pprint (db/all-products-by-slug (d/db conn) "/novo_computador"))
(pprint (db/all-products-by-slug (d/db conn) "/cellphone"))
(pprint (db/all-slugs (d/db conn)))
(pprint (db/all-names-prices (d/db conn)))
(pprint (db/all-names-prices-map-keys (d/db conn)))
(pprint (db/all-products-map-selecting (d/db conn)))
(pprint (db/all-products-map (d/db conn)))


(db/delete-db)
