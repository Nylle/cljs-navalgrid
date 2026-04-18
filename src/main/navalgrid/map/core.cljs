(ns navalgrid.map.core
  (:require [navalgrid.domain.square :as s]
            [navalgrid.persistence.repository :as repo]
            [navalgrid.map.maplibre :as m]
            [navalgrid.math :as math]))

(def map-properties {:style              "/marinequadratkarte.json"
                     :center             [0 40]
                     :zoom               1
                     :attributionControl false})

(defn create-fn
  "Returns a fn that creates a new map singleton."
  [parent loaded-fn moved-fn] (fn [_] (m/create! parent map-properties loaded-fn moved-fn)))

(defn destroy-fn
  "Returns a fn that destroys the previously created map singleton."
  [] (fn [_] (m/destroy!)))

(defn coord<->lngLat [[x y]] [y x])

(defn scale-denominator []
  (let [lat (second (m/get-center))
        zoom (m/get-zoom)]
    (math/round -2 (m/get-scale-denominator lat zoom))))

(defn bounds
  "Returns a vector of the SW and NE coordinates of the smallest possible enclosing rectangle for the provided square."
  [square]
  (let [[nw _ se _] (s/bounds square)]
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
  (let [coords (or poly (s/bounds {:nw nw :se se}))]
    (->> (conj coords (first coords))
         (map coord<->lngLat)
         (fix-for-antimeridian))))

(defn polygon->geojson [lnglats]
  {:type "geojson",
   :data {:type       "Feature",
          :geometry   {:type        "Polygon",
                       :coordinates [lnglats]},
          :properties {}}})

(defn polygons->geojson [polygons]
  {:type "geojson"
   :data {:type     "FeatureCollection"
          :features (for [lnglats polygons]
                      {:type       "Feature"
                       :geometry   {:type        "Polygon"
                                    :coordinates [lnglats]}
                       :properties {}})}})

(defn labels->geojson [labels]
  {:type "geojson"
   :data {:type     "FeatureCollection"
          :features (map (fn [{:keys [lng lat label]}]
                           {:type       "Feature"
                            :geometry   {:type "Point" :coordinates [lng lat]}
                            :properties {:label label}})
                         labels)}})

(defn draw-all-large-squares! []
  (let [squares (repo/all-large-squares)
        poly-src (->> (mapv square->polygon squares) (polygons->geojson))
        label-src (->> (map (fn [s] (let [center (s/center-coord s)] {:lng (second center) :lat (first center) :label (:id s)})) squares) (labels->geojson))
        poly-id "all-squares"
        label-id "all-labels"]
    (m/add-source! poly-id poly-src)
    (m/add-source! label-id label-src)
    (m/add-layer! {:id     poly-id
                   :type   "line"
                   :source poly-id
                   :layout {:line-cap "square"}
                   :paint  {:line-color "#038D3C"
                            :line-width 1}})
    (m/add-layer! {:id     label-id
                   :type   "symbol"
                   :source label-id
                   :layout {:text-field  ["get" "label"]
                            :text-font   ["Noto Sans Bold"]
                            :text-size   10
                            :text-offset [0 0]
                            :text-anchor "center"}
                   :paint  {:text-color      "#038D3C"
                            :text-halo-color "#fff"
                            :text-halo-width 1}})))

(defn set-square! [square]
  (let [outer "outer" inner "inner"]
    (m/clear-markers!)
    (m/remove-layer! outer)
    (m/remove-source! outer)
    (m/remove-layer! inner)
    (m/remove-source! inner)
    (if square
      (let [subs (:sub-squares square)]
        (m/hide-layer! "all-squares")
        (m/hide-layer! "all-labels")
        (m/add-source! outer (-> (square->polygon square) (polygon->geojson)))
        (m/add-layer! {:id     outer
                       :type   "line"
                       :source outer
                       :layout {:line-cap "square"}
                       :paint  {:line-color "#038D3C"
                                :line-width 3}})
        (m/add-marker! "marker-outer" (m/create-marker (:id square) (coord<->lngLat (:label square)) "marker-outer"))
        (m/add-source! inner (->> (mapv square->polygon subs) (polygons->geojson)))
        (m/add-layer! {:id     inner
                       :type   "line"
                       :source inner
                       :layout {:line-cap "square"}
                       :paint  {:line-color "#038D3C"
                                :line-width 2}})
        (m/fit-bounds! (map coord<->lngLat (bounds square))))
      (do
        (m/show-layer! "all-labels")
        (m/show-layer! "all-squares")
        (m/set-center! [0 40])
        (m/set-zoom! 1)))))