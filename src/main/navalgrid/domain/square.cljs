(ns navalgrid.domain.square
  (:require [navalgrid.utils :as utils]
            [navalgrid.math :as math]
            [navalgrid.domain.geo :as geo]))

(defn steps
  "Returns [e s] with the number of steps to go in eastern or southern direction to select one of nine sub-squares or
  in custom layout `sub`."
  [n sub]
  (if sub
    (let [i (utils/index-of (vec (flatten sub)) n)]
      (when i
        [(mod i (count (first sub))) (quot i (count (first sub)))]))
    [(mod (dec n) 3) (quot (dec n) 3)]))

(defn shift
  "Returns square after shifting it by factor in the direction of orientation."
  [square orientation factor]
  (let [{[nw-lat nw-lon] :nw [se-lat se-lon] :se} square]
    (cond
      (= :h orientation)
      (let [dLon (math/to-degrees (geo/smallest-lon-diff (math/to-radians nw-lon) (math/to-radians se-lon)))
            dist (* factor dLon)]
        (-> square
            (assoc-in [:nw 1] (geo/normalize-180 (math/round 3 (+ nw-lon dist))))
            (assoc-in [:se 1] (geo/normalize-180 (math/round 3 (+ se-lon dist))))))

      (= :v orientation)
      (let [dLat (- se-lat nw-lat)
            dist (* factor dLat)]
        (-> square
            (assoc-in [:nw 0] (math/round 3 (+ nw-lat dist)))
            (assoc-in [:se 0] (math/round 3 (+ se-lat dist)))))

      :default square)))

(defn def->sub
  "Returns sub-square n for provided square definition.
  Example: sub-square 5 for square AK1 would be AK15"
  [{:keys [id nw se sub]} n]
  (let [[e s] (if sub [(count (first sub)) (count sub)] [3 3])
        [_ lon-e] (second (geo/simple-rhumb-division nw [(first nw) (second se)] e))
        [lat-s _] (second (geo/simple-rhumb-division nw [(first se) (second nw)] s))]
    (when-let [[h v] (steps n sub)]
      (-> {:id (str id n) :nw nw :se [lat-s lon-e]}
          (shift :h h)
          (shift :v v)))))

(defn def->regular
  "Returns 3-by-3-square that matches reference ref by calculating from definition def.
  Example: for ref CG1234 and def CG, the sub-square will be calculated based on CG
  Example 2: for ref CG and def CG, def will be returned"
  [ref def]
  (loop [refs (map utils/str->int (drop (count (:id def)) ref))
         res def]
    (if (or (nil? res) (empty? refs))
      res
      (recur (rest refs) (def->sub res (first refs))))))

(defn two-by-five-subs [so]
  (if (= so :v)
    [[1 2] [3 4] [5 6] [7 8] [9 10]]
    [[1 2 3 4 5] [6 7 8 9 10]]))

(defn def->2by5
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
    (when-let [[h v] (steps n (two-by-five-subs so))]
      (def->regular
        ref
        (-> {:id (apply str (take 4 ref)) :nw nw :se [lat-s lon-e]}
            (shift :h h)
            (shift :v v))))))

(defn def->square
  "Returns the square with reference ref calculated from definition def."
  [ref def]
  (when def
    (cond
      (= (:id def) ref) def
      (:so def) (def->2by5 ref def)
      :default (def->regular ref def))))

(defn sub-square-refs
  "Returns a collection of square references for all theoretically possible sub-squares of ref.
  Squares matching the reference may not necessarily exist, e.g. for partial squares."
  [ref two-by-five?]
  (let [n (count ref)
        i (last ref)]
    (when (and (>= n 2) (< n 6))
      (if two-by-five?
        (map #(str (subs ref 0 (dec (count ref))) %) (cons (str "0" i) (map #(+ (* 10 i) %) (range 1 10))))
        (map #(str ref %) [1 2 3 4 5 6 7 8 9])))))

(defn nw-se
  "Returns a vector of the NW and SE coordinates of the smallest possible enclosing rectangle for the provided square."
  [{:keys [nw se poly]}]
  (if poly
    (let [lats (map first poly)
          lons (map second poly)]
      [[(apply max lats) (apply min lons)]
       [(apply min lats) (apply max lons)]])
    [nw se]))

(defn bounds
  "Returns a vector of the NW, NE, SE, and SW coordinates of the smallest possible enclosing rectangle for the provided square."
  [square]
  (let [[nw se] (nw-se square)
        ne [(first nw) (second se)]
        sw [(first se) (second nw)]]
    [nw ne se sw]))

(defn contains-coords?
  "Returns whether coordinates lat and lon are within the boundary of square."
  [square [lat lon]]
  (when square
    (let [nw-se (nw-se square)]
      (and (geo/contains-lat? nw-se lat) (geo/contains-lon? nw-se lon)))))

(defn center-coord
  "Returns the center coord of the bounds of the provided square."
  [square]
  (let [[nw ne _ sw] (bounds square)
        h (second (second (geo/simple-rhumb-division nw ne 2)))
        v (first (second (geo/simple-rhumb-division nw sw 2)))]
    [(math/round 3 v) (math/round 3 h)]))

(defn dimensions
  "Returns the dimensions (height, width) of the bounds of the provided square."
  [square]
  (let [[nw ne se sw] (bounds square)
        u-width (geo/rhumb-distance nw ne)
        l-width (geo/rhumb-distance sw se)]
    {:height     (math/round 2 (geo/rhumb-distance nw sw))
     :max-width  (math/round 2 (max u-width l-width))
     :min-width  (math/round 2 (min u-width l-width))
     :mean-width (math/round 2 (/ (+ u-width l-width) 2))}))