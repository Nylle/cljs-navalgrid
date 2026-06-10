(ns navalgrid.domain.coords
  (:require [clojure.string :as str]
            [navalgrid.math :as math]
            [navalgrid.utils :refer [lpad num->str str->float]]))

(defn sign->hem [deg lat?]
  (cond
    (and lat? (pos? deg)) "N"
    (and lat? (neg? deg)) "S"
    (and (not lat?) (pos? deg)) "E"
    :else "W"))

(defn hem->sign [hem]
  (let [h (-> (or hem "") (str/trim) (str/upper-case))]
    (if (contains? #{"S" "W" "-"} h) -1 1)))

(defn format-coords [deg mode is-lat?]
  (let [hem (sign->hem deg is-lat?)
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

(defn coords->str [coords format]
  (when-let [[lat lon] coords]
    (str (format-coords lat format true) ", " (format-coords lon format false))))

(defn parse-dd
  "`-001.0001°` or `001.0001° N` (° is optional)"
  [s]
  (let [re (re-pattern "^([+-]?)(\\d{1,3}([.]\\d+)?)°?( ?[NOESW])?$")]
    (when-let [[_ sig dd _ hem] (re-matches re s)]
      (-> (or hem sig)
          (hem->sign)
          (* (str->float dd))))))

(defn parse-dmm
  "`-001°01.001'` or `001°01.001' N` (°/' can be any non-numeral)"
  [s]
  (let [re (re-pattern "^([+-])?(\\d{1,3})[^\\d]([0-5]?[0-9]([.]\\d+)?)[^\\dNOESW]?( ?[NOESW])?$")]
    (when-let [[_ sig deg min _ hem] (re-matches re s)]
      (-> (str->float min)
          (/ 60)
          (+ (str->float deg))
          (* (hem->sign (or hem sig)))))))

(defn parse-dms
  "`-001°01'01.01''` or `001°01'01.01'' N` (°/'/'' can be any non-numeral)"
  [s]
  (let [re (re-pattern "^([+-])?(\\d{1,3})[^\\d]([0-5]?[0-9])[^\\d]([0-5]?[0-9]([.]\\d+)?)(\"\"|[^\\dNOESW]{1}|'')?( ?[NOESW])?$")]
    (when-let [[_ sig deg min sec _ _ hem] (re-matches re s)]
      (-> (str->float sec)
          (/ 60)
          (+ (str->float min))
          (/ 60)
          (+ (str->float deg))
          (* (hem->sign (or hem sig)))))))

(defn parse-coords [s]
  (when s
    (loop [[f & rs] [parse-dd parse-dmm parse-dms]]
      (when f
        (let [res (f s)]
          (if (nil? res) (recur rs) (math/round 5 res)))))))

(defn str->coords [s]
  (when (and s (not= "" s))
    (let [res (->> (str/split s #",")
                   (map str/trim)
                   (remove str/blank?)
                   (map parse-coords)
                   (remove nil?)
                   vec)]
      (when (> (count res) 1)
        res))))