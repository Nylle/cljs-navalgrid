(ns navalgrid.web.map
  (:require [navalgrid.web.maplibre :as m]))

(def map-properties {:style  "marinequadratkarte.json"
                     :center [0 0]
                     :zoom   0})

(defn create-fn
  "Returns a fn that creates a new map singleton."
  [ref] (fn [_] (m/create! ref map-properties)))

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
        (empty? acc) (conj acc curr)
        (> (- (first curr) (first (last acc))) 180) (conj acc [(- (first curr) 360) (second curr)])
        (> (- (first (last acc)) (first curr)) 180) (conj acc [(+ (first curr) 360) (second curr)])
        :else (conj acc curr)))
    []
    lnglats))

(defn square->polygon
  "Returns a vector of lnglats for the provided square where the first coord is appended again to form a 'ring'."
  [{:keys [nw se poly]}]
  (let [coords (or poly (coords nw se))]
    (->> (conj coords (first coords))
         (map coord->lngLat)
         (fix-for-antimeridian))))

(defn polygon->geojson [lnglats]
  {:type "geojson",
   :data {:type       "Feature",
          :geometry   {:type        "Polygon",
                       :coordinates [lnglats]},
          :properties {}}}
  )

(defn polygons->geojson [polygons]
  {:type "geojson"
   :data {:type     "FeatureCollection"
          :features (for [lnglats polygons]
                      {:type       "Feature"
                       :geometry   {:type        "Polygon"
                                    :coordinates [lnglats]}
                       :properties {}})}})

(defn set-square! [square]
  (when square
    (let [outer "outer"
          inner "inner"
          subs (:sub-squares square)]
      (m/remove-layer! outer)
      (m/remove-source! outer)
      (m/add-source! outer (-> (square->polygon square) (polygon->geojson)))
      (m/add-layer! {:id     outer
                     :type   "line"
                     :source outer
                     :layout {:line-cap "square"}
                     :paint  {:line-color "#038D3C"
                              :line-width 3}})
      (m/remove-layer! inner)
      (m/remove-source! inner)
      (m/add-source! inner (->> (mapv square->polygon subs) (polygons->geojson)))
      (m/add-layer! {:id     inner
                     :type   "line"
                     :source inner
                     :layout {:line-cap "square"}
                     :paint  {:line-color "#038D3C"
                              :line-width 2}})
      (m/fit-bounds! (map coord->lngLat (bounds square))))))