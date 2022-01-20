(ns ecommerce-filtros.db.venda
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce-filtros.model :as model]
            [ecommerce-filtros.db.entidade :as db.entidade]
            [schema.core :as s]
            [clojure.walk :as walk]
            [clojure.set :as cset]))

(defn adiciona!
  [conn produto-id quantidade]
  (let [id (model/uuid)]
    (d/transact conn [{:db/id "venda"
                       :venda/produto [:produto/id produto-id]
                       :venda/quantidade quantidade
                       :venda/id id
                       }])
    id))
