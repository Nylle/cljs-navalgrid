(ns navalgrid.domain.coords
  (:require [navalgrid.math :as math]
            [navalgrid.utils :refer [lpad num->str]]))

(defn format-coord [deg mode is-lat?]
  (let [hem (cond
              (and is-lat? (pos? deg)) "N"
              (and is-lat? (neg? deg)) "S"
              (and (not is-lat?) (pos? deg)) "E"
              :else "W")
        abs-deg (math/fabs deg)
        d (int abs-deg)
        rem-mins (* 60 (- abs-deg d))
        m (int rem-mins)
        s (math/round (* 60 (- rem-mins m)))
        {s' :s m' :m d' :d} (let [carry-m (int (quot s 60))
                                  s2 (mod s 60)
                                  m2 (+ m carry-m)
                                  carry-d (int (quot m2 60))
                                  m3 (mod m2 60)
                                  d2 (+ d carry-d)]
                              {:s s2 :m m3 :d d2})
        n (if is-lat? 2 3)
        deg-str (lpad d' n "0")]
    (case mode
      :dd (str (lpad (num->str abs-deg 3) (if is-lat? 6 7) "0") "°" hem)
      :dmm (str deg-str "°" (lpad (num->str (+ m' (/ s' 60.0)) 2) 5 "0") "'" hem)
      :dms (str deg-str "°" (lpad m' 2 "0") "'" (lpad s' 2 "0") "\"" hem)
      :jerry (str deg-str " " (lpad (math/round (+ m' (/ s' 60.0))) 2 "0") hem)
      (str deg))))

(defn coords->str [[lat lon] format]
  (str (format-coord lat format true) ", " (format-coord lon format false)))
