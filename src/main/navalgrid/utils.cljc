(ns navalgrid.utils)

(defn finite? [x]
  #?(:clj  (and (number? x) (<= (- Double/MAX_VALUE) x Double/MAX_VALUE))
     :cljs (js/Number.isFinite x)))

(defn str->int [x]
  #?(:clj  (Integer/parseInt x)
     :cljs (js/parseInt x)))

(defn error [x]
  #?(:clj  (RuntimeException. ^String x)
     :cljs (js/Error. x)))

(defn seq-contains? [coll x]
  (some #(= x %) coll))

(defn seq-empty? [coll]
  (not (seq coll)))

(defn index-of [coll x]
  (some (fn [[i item]] (if (= x item) i))
        (map-indexed vector coll)))