(ns navalgrid.geo)

(defn fix-for-antimeridian [lnglats]
  (reduce
   (fn [acc curr]
     (cond
      (empty? acc)                                    (conj acc curr)
      (> (- (first curr) (first (last acc))) 180)     (conj acc [(- (first curr) 360) (second curr)])
      (> (- (first (last acc)) (first curr)) 180)     (conj acc [(+ (first curr) 360) (second curr)])
      :else                                           (conj acc curr)))
   []
   lnglats))

(defn polygon [lnglats]
  (fix-for-antimeridian (conj lnglats (first lnglats))))

