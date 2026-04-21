(ns navalgrid.utils)

(defn finite? [x]
  (js/Number.isFinite x))

(defn str->int [x]
  (js/parseInt x))

(defn lpad [s n pad]
  (.padStart (str s) n (str pad)))

(defn num->str [s digits]
  (.toFixed s digits))

(defn error [x]
  (js/Error. x))

(defn seq-contains? [coll x]
  (some #(= x %) coll))

(defn seq-empty? [coll]
  (not (seq coll)))

(defn index-of [coll x]
  (some (fn [[i item]] (if (= x item) i))
        (map-indexed vector coll)))