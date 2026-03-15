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

(defn steps
  "Returns [e s] with the number of steps to go in eastern or southern direction to select one of nine sub-squares or
  in custom layout `sub`."
  [n sub]
  (if (nil? sub)
    [(mod (dec n) 3) (quot (dec n) 3)]
    (let [i (core/index-of (vec (flatten sub)) n)]
      (when i
        [(mod i (count (first sub))) (quot i (count (first sub)))]))))

(defn map-to-model [def]
  (if-let [{:keys [id poly nw se]} def]
    (if (seq poly)
      {:id id :outer poly}
      {:id id :outer [nw [(first nw) (second se)] se [(first se) (second nw)]]})))

(defn sub-square [{:keys [id nw se sub]} n]
  (let [[e s] (if sub [(count (first sub)) (count sub)] [3 3])
        [_ lon-e] (second (geo/simple-rhumb-division nw [(first nw) (second se)] e))
        [lat-s _] (second (geo/simple-rhumb-division nw [(first se) (second nw)] s))]
    (if-let [[h v] (steps n sub)]
      (-> {:id (str id n) :nw nw :se [lat-s lon-e]}
          (shift :h h)
          (shift :v v)))))

(defn regular-square [id def]
  (loop [ids (map js/parseInt (drop (count (:id def)) id))
         square def]
    (if (empty? ids)
      square
      (recur (rest ids) (sub-square square (first ids))))))

(defn two-by-five-square
  "id: AB1234 def: {:id \"AL3\" :nw [60.9 -19.3] :se [56.4 -15.7] :so :v}"
  [id def]
  nil)

(defn from-square-def [id def]
  (if def
    (map-to-model (cond
                    (= (:id def) id) def
                    (:so def) (two-by-five-square id def)
                    :default (regular-square id def)))))