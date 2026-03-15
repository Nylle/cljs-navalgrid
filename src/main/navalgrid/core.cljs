(ns navalgrid.core
  (:require [cljs.math :as math]))

(defn finite? [^number number]
  (js/Number.isFinite number))

(defn seq-contains? [coll x]
  (some #(= x %) coll))

(defn seq-empty? [coll]
  (not (seq coll)))

(defn round [^number digits ^number number]
  (let [factor (apply * (take digits (repeat 10)))]
    (-> (* number factor)
        (math/round)
        (/ factor))))

(defn index-of [coll x]
  (some (fn [[i item]] (if (= x item) i))
        (map-indexed vector coll)))