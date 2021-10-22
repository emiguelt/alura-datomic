(ns ecommerce.aula01_1_2
  (:use clojure.pprint)
  (:require [ecommerce.db :as db]
            [ecommerce.model :as model]
            [datomic.api :as d]))

(def conn (db/open-connection))
(d/transact conn db/schema)

(let [computer (model/new-product "Novo computador" "/novo_computador" 4000.5M)]
  (d/transact conn [computer]))

;get an "image" of the database in that time
(def db (d/db conn))

(d/q '[:find ?entidade
       :where [?entidade :product/name]] db)


(let [computer (model/new-product "Novo computador" "/novo_computador" 4000.5M)]
  (d/transact conn [computer]))

; to get the new data a new read only connection must be created
(def db (d/db conn))

(d/q '[:find ?entidade
       :where [?entidade :product/name]] db)

; updating/removing data
(let [computer (model/new-product "Novo computador" "/novo_computador" 4000.5M)
      result @(d/transact conn [computer])
      entity-id (first (vals (:tempids result)))]
  (pprint result)
  ;updaate
  (pprint @(d/transact conn [[:db/add entity-id :product/price 100.0M]]))
  (pprint @(d/transact conn [[:db/retract entity-id :product/slug "/novo-computador"]]))
  )
