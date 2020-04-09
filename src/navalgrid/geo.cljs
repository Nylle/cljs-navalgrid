(ns navalgrid.geo)

(defn lnglat [coordinate]
  (let [lon (:Longitude coordinate)
        lat (:Latitude coordinate)]
    [lon lat]))

(defn fix-for-antimeridian [coordinates]
  (reduce
   (fn [acc curr]
     (cond
      (empty? acc)                                (conj acc curr)
      (> (- (first curr) (first (last acc))) 180) (conj acc [(- (first curr) 360) (first (rest curr))])
      (> (- (first (last acc)) (first curr)) 180) (conj acc [(+ (first curr) 360) (first (rest curr))])
      :else                                       (conj acc curr)))
   []
   coordinates))

(defn polygon [coordinates]
   (let [lnglats (mapv #(navalgrid.geo/lnglat %) coordinates)]
     (conj lnglats (first lnglats))))

