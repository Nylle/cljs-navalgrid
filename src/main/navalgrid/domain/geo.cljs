(ns navalgrid.domain.geo
  (:require [cljs.math :as math]))

(def earth-radius-km 6371)

(defn finite? [x]
  (js/Number.isFinite x))

(defn km->nmi
  "Converts kilometres to nautical miles."
  [km]
  (* km 0.539957))

(defn nmi->km
  "Converts nautical miles to kilometres."
  [nmi]
  (* nmi 1.852))

(defn deg->rad [degrees]
  (* degrees (/ math/PI 180)))

(defn isometric-lat-diff
  "Returns the isometric (projected) latitude difference. Latitudes have to be in rad.
  Source: http://www.movable-type.co.uk/scripts/latlong.html by Chris Veness"
  [lat1 lat2]
  (let [pi-quarter (/ math/PI 4)
        tan1 (math/tan (+ pi-quarter (/ lat1 2)))
        tan2 (math/tan (+ pi-quarter (/ lat2 2)))]
    (math/log (/ tan1 tan2))))

(defn smallest-lon-diff
  "Returns the smallest longitude difference. If it is greater than 180° take the shorter rhumb line across the
  anti-meridian. Longitudes have to be in rad.
  Source: http://www.movable-type.co.uk/scripts/latlong.html by Chris Veness"
  [lon1 lon2]
  (let [dLon (- lon2 lon1)]
    (if (> (math/fabs dLon) math/PI)
      (if (> dLon 0)
        (- (- (* 2 math/PI) dLon))
        (+ (* 2 math/PI) dLon))
      dLon)))

(defn mercator-comp
  "Since a rhumb line is a straight line on a Mercator projection, the distance between two points along a
  rhumb line is the length of that line (by Pythagoras); but the distortion of the projection needs to be
  compensated for. On a constant latitude course (travelling east-west), this compensation is cosφ; in the
  general case, it is Δφ/Δψ.
  Source: http://www.movable-type.co.uk/scripts/latlong.html by Chris Veness"
  [Δφ Δψ φ1]
  (let [res (/ Δφ Δψ)]
    (if (finite? res)
      res
      (math/cos φ1))))

(defn rhumb-distance
  "Returns the Rhumb-line distance in Nautical Miles between the given coords.
  A Rhumb line (or loxodrome) is a line crossing all meridians of longitude at the same angle, i.e. a path derived
  from a defined initial bearing.
  Source: http://www.movable-type.co.uk/scripts/latlong.html by Chris Veness"
  [coord1 coord2]
  (let [φ1 (deg->rad (:lat coord1))
        φ2 (deg->rad (:lat coord2))
        λ1 (deg->rad (:lon coord1))
        λ2 (deg->rad (:lon coord2))
        Δφ (- φ2 φ1)
        Δλ (smallest-lon-diff λ1 λ2)
        Δψ (isometric-lat-diff φ1 φ2)
        q (mercator-comp Δφ Δψ φ1)]
    (->> (+ (* Δφ Δφ) (* q q Δλ Δλ))
         (math/sqrt)
         (* earth-radius-km)
         (km->nmi))))

(defn normalize-lon
  "Returns the normalized longitude for lons outside -180°..180° otherwise lon.
  Example: a lon of 181° is being normalized to -179°."
  [lon]
  (cond
    (> lon 180) (- lon 360)
    (< lon -180) (+ lon 360)
    :default lon))

(defn lon-range
  "Returns a collection of longitudes along the shortest possible rhumb-line between lon1 and lon2 with a distance
  of (lon2-lon1)/div between the longitudes.
  Example: a rhumb-line between lon1=175 and lon2=-175 crossing the anti-meridian has a length of 10. With div=5
  the resulting collection is (175, 177, 179, -179, -177)."
  [lon1 lon2 div]
  (let [dLon (- lon2 lon1)
        range' #(range %1 %2 (/ (- %2 %1) div))]
    (cond
      (< dLon -180) (map normalize-lon (range' lon1 (+ lon1 (- 180 lon1) (+ 180 lon2))))
      (> dLon 180) (map normalize-lon (range' lon1 (- lon1 (- 180 lon2) (+ 180 lon1))))
      :default (range' lon1 lon2))))

(defn lat-range
  "Returns a collection of latitudes along a rhumb-line between lat1 and lat2 with a distance of (lat2-lat1)/div
  between the latitudes.
  Example: a rhumb-line between lat1=2 and lat2=-4 has a length of 6. With div=3 the resulting collection is
  (2, 0, -2)."
  [lat1 lat2 div]
  (range lat1 lat2 (/ (- lat2 lat1) div)))

(defn simple-rhumb-division
  "Divides a rhumb-line between coord1 and coord2 by div into equal parts and returns the respective coordinates.
  This only works for strictly horizontal or vertical rhumb-lines with bearings of 0°, 90°, 180°, 270°."
  [coord1 coord2 div]
  (let [lat1 (:lat coord1)
        lat2 (:lat coord2)
        lon1 (:lon coord1)
        lon2 (:lon coord2)]
    (cond
      (= lat1 lat2) (map (fn [x] {:lat lat1 :lon x}) (lon-range lon1 lon2 div))
      (= lon1 lon2) (map (fn [x] {:lat x :lon lon1}) (lat-range lat1 lat2 div))
      :else (throw (js/Error. (str "Invalid bearing from " coord1 " to " coord2 ". Must be one of 0°, 90°, 180°, 270°."))))))
