(ns ecommerce-filtros.aula3
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce-filtros.db.config :as db.config]
            [ecommerce-filtros.db.produto :as db.produto]
            [ecommerce-filtros.db.venda :as db.venda]
            [schema.core :as s]))

(s/set-fn-validation! true)

(db.config/apaga-banco!)
(def conn (db.config/abre-conexao!))
(db.config/cria-schema! conn)
(db.config/cria-dados-de-exemplo! conn)

(def produtos (db.produto/todos (d/db conn)))

(def primeiro (first produtos))
(pprint primeiro)

(def venda1 (db.venda/adiciona! conn (:produto/id primeiro) 3 ))
(pprint venda1)

(pprint (db.venda/custo (d/db conn) venda1))

(pprint @(db.produto/adiciona-ou-altera! conn [{:produto/id (:produto/id primeiro)
                                                :produto/preco 300M}]))

(pprint (db.venda/custo (d/db conn) venda1))


