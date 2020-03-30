(ns navalgrid.mapbox)

(defn create-map [canvas & [center]]
  (js/createMap (reagent.dom/dom-node canvas) 0 (clj->js (or center [0 0]))))

(defn on-load [map f]
  (.on map "load" f))

(defn add-square [map id width coordinates]
  (.addLayer map
             (clj->js
              {:id     id,
               :type   "line",
               :source
               {:type "geojson",
                :data
                {:type "Feature",
                 :geometry
                 {:type "LineString", :coordinates coordinates}}},
               :layout {:line-cap "square"},
               :paint  {:line-color "#038D3C", :line-width width}})))

(defn fit-bounds [map coordinates]
  (.fitBounds map (clj->js coordinates) (clj->js {:padding 200})))