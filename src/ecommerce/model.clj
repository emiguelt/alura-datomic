(ns ecommerce.model
  (:require [schema.core :as s])
  (:import (java.util UUID)))

(defn uuid [] (UUID/randomUUID))

(defn new-product
  ([name slug price]
   (new-product (uuid) name slug price))
  ([uuid name slug price]
   {:product/id    uuid
    :product/name  name
    :product/slug  slug
    :product/price price})

  )

(defn new-category
  ([name]
   (new-category (uuid) name))
  ([uuid name]
   {
    :category/id   uuid
    :category/name name
    }))

(def Category
  {:category/name s/Str
   :category/id UUID})

(def Product
  {:product/name                 s/Str
   :product/slug                 s/Str
   :product/price                BigDecimal
   :product/id                   UUID
   (s/optional-key :product/tag) [s/Str]
   (s/optional-key :product/category) Category})