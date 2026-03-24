(ns navalgrid.web.map
  (:require [navalgrid.web.maplibre :as m]))

(defn create-fn
  "Returns a fn that creates a new map singleton."
  [ref] (fn [_] (m/create! ref)))

(defn destroy-fn
  "Returns a fn that destroys the previously created map singleton."
  [] (fn [_] (m/destroy!)))

(defn coord->lngLat
  "Returns a vector of the longitude and latitude for the provided [lat lon]."
  [[lat lon]]
  [lon lat])

(defn coords
  "Returns a vector of all four coordinates for a rectangle defined by the provided NW and SE coordinates."
  [nw se]
  [nw [(first nw) (second se)] se [(first se) (second nw)]])

(defn bounds
  "Returns a vector of the SW and NE coordinates of the smallest possible enclosing rectangle for the provided square."
  [{:keys [nw se poly]}]
  (if poly
    (let [lats (map first poly)
          lons (map second poly)]
      [[(apply min lats) (apply min lons)] [(apply max lats) (apply max lons)]])
    [[(first se) (second nw)] [(first nw) (second se)]]))

(defn fix-for-antimeridian
  "Returns a vector of lnglats where values on opposite sides of the antimeridian (180°) are de-normalized in order for
  maplibregl to render polygons properly.
  Example: for [[-160 90] [170 90]] the lon of 170 is being changed to -190 despite not being within the usual bounds of
  -180..180."
  [lnglats]
  (reduce
    (fn [acc curr]
      (cond
        (empty? acc)                                    (conj acc curr)
        (> (- (first curr) (first (last acc))) 180)     (conj acc [(- (first curr) 360) (second curr)])
        (> (- (first (last acc)) (first curr)) 180)     (conj acc [(+ (first curr) 360) (second curr)])
        :else                                           (conj acc curr)))
    []
    lnglats))

(defn polygon
  "Returns a vector of lnglats for the provided coords where the first coord is appended again to form a 'ring'."
  [coords]
  (->> (conj coords (first coords))
       (map coord->lngLat)
       (fix-for-antimeridian)))

(defn square->geojson [{:keys [nw se poly]}]
  (let [coords (or poly (coords nw se))
        lnglats (polygon coords)]
    {:type "geojson"
     :data {:type       "Feature"
            :geometry   {:type        "Polygon"
                         :coordinates [lnglats]}
            :properties {}}}))

(defn set-square! [square]
  (let [id "outer"]
    (m/remove-layer! id)
    (m/remove-source! id)
    (m/add-source! id (square->geojson square))
    (m/add-layer! {:id     id
                   :type   "line"
                   :source id
                   :layout {:line-cap "square"}
                   :paint  {:line-color "#000000"
                            :line-width 2}})
    (m/fit-bounds! (map coord->lngLat (bounds square)))))