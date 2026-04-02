(ns navalgrid.math
  (:require #?(:clj  [clj.math :as math]
               :cljs [cljs.math :as math])))

(def PI math/PI)
(def cos math/cos)
(def sin math/sin)
(def atan2 math/atan2)
(def tan math/tan)
(def log math/log)
(def log10 math/log10)
(def floor math/floor)
(def fabs math/fabs)
(def sqrt math/sqrt)
(def pow math/pow)
(def to-radians math/to-radians)
(def to-degrees math/to-degrees)
(defn round
  ([x] (math/round x))
  ([digits x]
   (if (< digits 0)
     (let [pow   (int (math/floor (math/log10 (math/fabs x))))
           scale (math/pow 10.0 (- pow (dec (math/fabs digits))))]
       (-> (/ x scale)
           (math/round)
           (* scale)))
     (let [factor (apply * (take digits (repeat 10)))]
       (-> (* x factor)
           (math/round)
           (/ factor))))))