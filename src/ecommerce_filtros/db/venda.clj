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

(defn instante-da-venda [db venda-id]
  (d/q '[:find ?instante .
         :in $ ?id
         :where [_ :venda/id ?id ?tx true]
         [?tx :db/txInstant ?instante]] db venda-id))

;; calcula o custo da venda no instante que foi creada
(defn custo [db venda-id]
  (let [instante (instante-da-venda db venda-id)]
    (d/q '[:find (sum ?preco-por-quantidade) .
           :in $ in
           :where [?venda :venda/id ?id]
           [?venda :venda/quantidade ?quantidade]
           [?venda :venda/produto ?produto]
           [?produto :produto/preco ?preco]
           [(* ?preco ?quantidade) ?preco-por-quantidade]]
      (d/as-of db instante) venda-id)))
