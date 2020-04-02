(ns navalgrid.mapbox)

(defn create-map [canvas & [center]]
  (js/createMap (reagent.dom/dom-node canvas) 0 (clj->js (or center [0 0]))))

(defn on-load [map f]
  (.on map "load" f))

(defn remove-square [map id]
  (if (.getLayer map id) (.removeLayer map id))
  (if (.getSource map id) (.removeSource map id)))

(defn fit-bounds [map coordinates]
  (.fitBounds map (clj->js coordinates) (clj->js {:padding 200})))

(defn add-square [map id width coordinates]
  (.addSource map
              id
              (clj->js
               {:type "geojson",
                :data
                {:type "Feature",
                 :geometry
                 {:type "MultiLineString", :coordinates coordinates}}}))
  (.addLayer map
             (clj->js
              {:id     id,
               :type   "line",
               :source id,
               :layout {:line-cap "square"},
               :paint  {:line-color "#038D3C", :line-width width}})))

(defn set-square [map square]
  (remove-square map "outer")
  (remove-square map "inner")
  (add-square map "outer" 3 (:OuterCoordinates square))
  (add-square map "inner" 2 (:InnerCoordinates square))
  (fit-bounds map (:Bounds square)))

