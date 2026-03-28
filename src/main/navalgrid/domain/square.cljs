(ns navalgrid.domain.square
  (:require [navalgrid.utils :as utils]
            [navalgrid.math :as math]
            [navalgrid.domain.geo :as geo]))

(defn shift
  "Returns square after shifting it by factor in the direction of orientation."
  [square orientation factor]
  (let [{[nw-lat nw-lon] :nw [se-lat se-lon] :se} square]
    (cond
      (= :h orientation)
      (let [dLon (math/to-degrees (geo/smallest-lon-diff (math/to-radians nw-lon) (math/to-radians se-lon)))
            dist (* factor dLon)]
        (-> square
            (assoc-in [:nw 1] (geo/normalize-lon (math/round 3 (+ nw-lon dist))))
            (assoc-in [:se 1] (geo/normalize-lon (math/round 3 (+ se-lon dist))))))

      (= :v orientation)
      (let [dLat (- se-lat nw-lat)
            dist (* factor dLat)]
        (-> square
            (assoc-in [:nw 0] (math/round 3 (+ nw-lat dist)))
            (assoc-in [:se 0] (math/round 3 (+ se-lat dist)))))

      :default square)))

(defn steps
  "Returns [e s] with the number of steps to go in eastern or southern direction to select one of nine sub-squares or
  in custom layout `sub`."
  [n sub]
  (if (nil? sub)
    [(mod (dec n) 3) (quot (dec n) 3)]
    (let [i (utils/index-of (vec (flatten sub)) n)]
      (when i
        [(mod i (count (first sub))) (quot i (count (first sub)))]))))

(defn sub-square
  "Returns sub-square n for provided square.
  Example: sub-square 5 for square AK1 would be AK15"
  [{:keys [id nw se sub]} n]
  (let [[e s] (if sub [(count (first sub)) (count sub)] [3 3])
        [_ lon-e] (second (geo/simple-rhumb-division nw [(first nw) (second se)] e))
        [lat-s _] (second (geo/simple-rhumb-division nw [(first se) (second nw)] s))]
    (if-let [[h v] (steps n sub)]
      (-> {:id (str id n) :nw nw :se [lat-s lon-e]}
          (shift :h h)
          (shift :v v)))))

(defn regular-square
  "Returns 3-by-3-square that matches reference ref by calculating from definition def.
  Example: for ref CG1234 and def CG, the sub-square will be calculated based on CG
  Example 2: for ref CG and def CG, def will be returned"
  [ref def]
  (loop [refs (map utils/str->int (drop (count (:id def)) ref))
         square def]
    (if (or (nil? square) (empty? refs))
      square
      (recur (rest refs) (sub-square square (first refs))))))

(defn two-by-five-subs [o]
  (if (= o :v)
    [[1 2] [3 4] [5 6] [7 8] [9 10]]
    [[1 2 3 4 5] [6 7 8 9 10]]))

(defn two-by-five-square
  "Returns 2-by-5-square that matches reference ref by calculating from definition def.
  Example: for ref CG1234 and def CG, the sub-square will be calculated based on CG
  Example 2: for ref CG and def CG, def will be returned"
  [ref def]
  (let [{:keys [nw se so]} def
        [e s] (if (= so :v) [2 5] [5 2])
        [_ lon-e] (second (geo/simple-rhumb-division nw [(first nw) (second se)] e))
        [lat-s _] (second (geo/simple-rhumb-division nw [(first se) (second nw)] s))
        refs (map utils/str->int (seq (drop 2 ref)))
        n (if (= 0 (first refs)) 10 (second refs))]
    (if-let [[h v] (steps n (two-by-five-subs so))]
      (regular-square
        ref
        (-> {:id (apply str (take 4 ref)) :nw nw :se [lat-s lon-e]}
            (shift :h h)
            (shift :v v))))))

(defn cleanup [def]
  (dissoc def :sub))

(defn from-square-def [ref def]
  (when def
    (cleanup
      (cond
        (= (:id def) ref) def
        (:so def) (two-by-five-square ref def)
        :default (regular-square ref def)))))

(defn sub-square-refs [ref two-by-five?]
  (let [n (count ref)]
    (when (and (>= n 2) (< n 6))
      (if two-by-five?
        (map #(str (subs ref 0 (dec (count ref))) %) ["01" 11 12 13 14 15 16 17 18 19])
        (map #(str ref %) [1 2 3 4 5 6 7 8 9])))))