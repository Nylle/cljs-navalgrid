(ns navalgrid.domain.square
  (:require [navalgrid.core :as core]
            [navalgrid.domain.geo :as geo]
            [cljs.math :as math]))

(defn shift [square orientation factor]
  (let [{[nw-lat nw-lon] :nw [se-lat se-lon] :se} square]
    (cond
      (= :h orientation)
      (let [dLon (math/to-degrees (geo/smallest-lon-diff (math/to-radians nw-lon) (math/to-radians se-lon)))
            dist (* factor dLon)]
        (-> square
            (assoc-in [:nw 1] (geo/normalize-lon (core/round 3 (+ nw-lon dist))))
            (assoc-in [:se 1] (geo/normalize-lon (core/round 3 (+ se-lon dist))))))

      (= :v orientation)
      (let [dLat (- se-lat nw-lat)
            dist (* factor dLat)]
        (-> square
            (assoc-in [:nw 0] (core/round 3 (+ nw-lat dist)))
            (assoc-in [:se 0] (core/round 3 (+ se-lat dist)))))

      :default square)))

;(defn xxx [square]
;  (let [h-lat ])
;  )

;(defn recursive [square s]
;  (if (core/seq-empty? s)
;    square
;    (recur square )))

(defn regular [square ref]
  (if (seq (:s ref))
    {}
    {:ref ref :nw (:nw square) :se (:se square)}))