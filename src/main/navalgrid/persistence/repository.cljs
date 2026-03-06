(ns navalgrid.persistence.repository
  (:require [navalgrid.domain.square :as square]
            [navalgrid.persistence.data :as data]
            [navalgrid.core :as core]))

(defn find-in-group [{:keys [id nw se o] :or {o :h}} ref]
  (let [i (.indexOf id (:l ref))]
    (if (= i -1) nil (square/shift {:nw nw :se se} o i))))

(defn regular-square [group ref]
  (if (seq (:s ref))
    "sub-square"
    (as-> (find-in-group group ref) square
          {:ref ref :nw (:nw square) :se (:se square)})))

(defn find-by-ref
  "ref: {:l \"AB\" :s [1 2 3 4]}"
  [ref]
  (let [large (->> (concat data/large-regular-squares data/large-partial-squares)
                   (filter #(core/seq-contains? (:id %) (:l ref)))
                   (take 1))]
    (if (seq large)
      (regular-square (first large) ref)
      nil)

    ))