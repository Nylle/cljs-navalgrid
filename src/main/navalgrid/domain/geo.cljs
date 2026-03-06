(ns navalgrid.domain.geo
  (:require [cljs.math :as math]
            [navalgrid.core :as core]))

(def earth-radius-nmi 3440)

(defn lat [[lat _]] lat)

(defn lon [[_ lon]] lon)

(defn isometric-lat-diff
  "Returns the isometric (projected) difference between latitudes lat1 and lat2. Latitudes have to be in rad.
  Source: http://www.movable-type.co.uk/scripts/latlong.html by Chris Veness"
  [^number lat1 ^number lat2]
  (let [pi-quarter (/ math/PI 4)
        tan1 (math/tan (+ pi-quarter (/ lat1 2)))
        tan2 (math/tan (+ pi-quarter (/ lat2 2)))]
    (math/log (/ tan1 tan2))))

(defn smallest-lon-diff
  "Returns the smallest difference between longitudes lon1 and lon2. If it is greater than 180° take the shorter rhumb
  line across the anti-meridian. Longitudes have to be in rad.
  Source: http://www.movable-type.co.uk/scripts/latlong.html by Chris Veness"
  [^number lon1 ^number lon2]
  (let [dLon (- lon2 lon1)]
    (if (> (math/fabs dLon) math/PI)
      (if (> dLon 0)
        (- (- (* 2 math/PI) dLon))
        (+ (* 2 math/PI) dLon))
      dLon)))

(defn mercator-comp
  "Returns a compensation factor for the mercator projection distortion.
  Since a rhumb line is a straight line on a Mercator projection, the distance between two points along a
  rhumb line is the length of that line (by Pythagoras); but the distortion of the projection needs to be
  compensated for. On a constant latitude course (travelling east-west), this compensation is cosφ where φ is the
  latitute (lat1); in the general case, it is Δφ/Δψ where Δφ is the geodetic latitude-difference (dLat) and Δψ is the
  isometric latitude difference (dLat').
  Source: http://www.movable-type.co.uk/scripts/latlong.html by Chris Veness"
  [^number dLat ^number dLat' ^number lat1]
  (let [res (/ dLat dLat')]
    (if (core/finite? res)
      res
      (math/cos lat1))))

(defn rhumb-distance
  "Returns the length in Nautical Miles for a rhumb line between the coordinates coord1 and coord2.
  A rhumb line (or loxodrome) is a line crossing all meridians of longitude at the same angle, i.e. a path derived
  from a defined initial bearing.
  Source: http://www.movable-type.co.uk/scripts/latlong.html by Chris Veness"
  [coord1 coord2]
  (let [lat1 (math/to-radians (lat coord1))
        lat2 (math/to-radians (lat coord2))
        lon1 (math/to-radians (lon coord1))
        lon2 (math/to-radians (lon coord2))
        dLat (- lat2 lat1)
        dLon (smallest-lon-diff lon1 lon2)
        dLon' (isometric-lat-diff lat1 lat2)]
    (as-> (mercator-comp dLat dLon' lat1) x
          (* x x dLon dLon)
          (+ x (* dLat dLat))
          (math/sqrt x)
          (* x earth-radius-nmi))))

(defn haversine-distance
  "Returns the length in Nautical Miles for a great-circle line between the coordinates coord1 and coord2 using the
  'haversine' formula.
  A great-circle line is the shortest connection between two points over the earth's surface with a constantly changing
  bearing.
  Source: http://www.movable-type.co.uk/scripts/latlong.html by Chris Veness"
  [coord1 coord2]
  (let [lat1 (math/to-radians (lat coord1))
        lat2 (math/to-radians (lat coord2))
        lon1 (math/to-radians (lon coord1))
        lon2 (math/to-radians (lon coord2))
        dLat-2 (/ (- lat2 lat1) 2)
        dLon-2 (/ (- lon2 lon1) 2)]
    (as-> (math/sin dLon-2) x
          (* x x (math/cos lat1) (math/cos lat2))
          (+ x (* (math/sin dLat-2) (math/sin dLat-2)))
          (* 2 (math/atan2 (math/sqrt x) (math/sqrt (- 1 x))))
          (* x earth-radius-nmi))))

(defn normalize-lon
  "Returns the normalized value for longitude lon if outside -180°..180° otherwise lon.
  Example: a lon of 181° is being normalized to -179°."
  [^number lon]
  (cond
    (> lon 180) (- lon 360)
    (< lon -180) (+ lon 360)
    :default lon))

(defn lon-range
  "Returns a collection of longitudes along the shortest possible rhumb line between longitudes lon1 and lon2 with a
  distance of (lon2-lon1)/div between them.
  Example: a rhumb line between lon1=175 and lon2=-175 crossing the anti-meridian has a length of 10. With div=5
  the resulting collection is (175, 177, 179, -179, -177)."
  [^number lon1 ^number lon2 ^number div]
  (let [dLon (- lon2 lon1)
        range' #(range %1 %2 (/ (- %2 %1) div))]
    (cond
      (< dLon -180) (map normalize-lon (range' lon1 (+ lon1 (- 180 lon1) (+ 180 lon2))))
      (> dLon 180) (map normalize-lon (range' lon1 (- lon1 (+ 180 lon1) (- 180 lon2))))
      :default (range' lon1 lon2))))

(defn lat-range
  "Returns a collection of latitudes along a rhumb line between latitudes lat1 and lat2 with a distance of
  (lat2-lat1)/div between them.
  Example: a rhumb line between lat1=2 and lat2=-4 has a length of 6. With div=3 the resulting collection is
  (2, 0, -2)."
  [^number lat1 ^number lat2 ^number div]
  (range lat1 lat2 (/ (- lat2 lat1) div)))

(defn simple-rhumb-division
  "Divides a rhumb line between coordinates coord1 and coord2 by div into equal parts and returns the respective coordinates.
  This only works for strictly horizontal or vertical rhumb lines with bearings of 0°, 90°, 180°, 270°."
  [coord1 coord2 ^number div]
  (let [[lat1 lon1] coord1
        [lat2 lon2] coord2]
    (cond
      (= lat1 lat2) (map (fn [x] [lat1 x]) (lon-range lon1 lon2 div))
      (= lon1 lon2) (map (fn [x] [x lon1]) (lat-range lat1 lat2 div))
      :else (throw (js/Error. (str "Invalid bearing from " coord1 " to " coord2 ". Must be one of 0°, 90°, 180°, 270°."))))))
