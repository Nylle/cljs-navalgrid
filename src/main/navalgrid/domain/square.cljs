(ns navalgrid.domain.square
  (:require [navalgrid.core :as core]
            [navalgrid.domain.geo :as geo]
            [cljs.math :as math]
            [cljs.pprint :refer [pprint]]))

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

(defn cleanup [def]
  (dissoc def :sub))

(defn sub-square [{:keys [id nw se sub]} n]
  (let [[e s] (if sub [(count (first sub)) (count sub)] [3 3])
        [_ lon-e] (second (geo/simple-rhumb-division nw [(first nw) (second se)] e))
        [lat-s _] (second (geo/simple-rhumb-division nw [(first se) (second nw)] s))]
    (if-let [[h v] (steps n sub)]
      (-> {:id (str id n) :nw nw :se [lat-s lon-e]}
          (shift :h h)
          (shift :v v)))))

(defn regular-square [ref def]
  (loop [refs (map js/parseInt (drop (count (:id def)) ref))
         square def]
    (if (empty? refs)
      square
      (recur (rest refs) (sub-square square (first refs))))))

(defn two-by-five-subs [o]
  (if (= o :v)
    [[1 2] [3 4] [5 6] [7 8] [9 10]]
    [[1 2 3 4 5] [6 7 8 9 10]]))

(defn two-by-five-square [ref def]
  (let [{:keys [nw se so]} def
        [e s] (if (= so :v) [2 5] [5 2])
        [_ lon-e] (second (geo/simple-rhumb-division nw [(first nw) (second se)] e))
        [lat-s _] (second (geo/simple-rhumb-division nw [(first se) (second nw)] s))
        refs (map js/parseInt (seq (drop 2 ref)))
        n (if (= 0 (first refs)) 10 (second refs))]
    (if-let [[h v] (steps n (two-by-five-subs so))]
      (regular-square
        ref
        (-> {:id (apply str (take 4 ref)) :nw nw :se [lat-s lon-e]}
            (shift :h h)
            (shift :v v))))))

(defn from-square-def [ref def]
  (if def
    (cleanup
      (cond
        (= (:id def) ref) def
        (:so def) (two-by-five-square ref def)
        :default (regular-square ref def)))))