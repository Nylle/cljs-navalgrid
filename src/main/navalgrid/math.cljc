(ns navalgrid.math
  (:require #?(:clj  [clj.math :as math]
               :cljs [cljs.math :as math])))

(def PI math/PI)
(def cos math/cos)
(def sin math/sin)
(def atan2 math/atan2)
(def tan math/tan)
(def log math/log)
(def fabs math/fabs)
(def sqrt math/sqrt)
(def to-radians math/to-radians)
(def to-degrees math/to-degrees)
(defn round
  ([x] (math/round x))
  ([digits x] (let [factor (apply * (take digits (repeat 10)))]
                (-> (* x factor)
                    (math/round)
                    (/ factor)))))