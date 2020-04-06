(ns ^:figwheel-hooks navalgrid.mapbox
  (:require [reagent.core :as r]))

(defonce markers (r/atom []))

(defn create-map [canvas & [center]]
  (js/createMap (reagent.dom/dom-node canvas) 0 (clj->js (or center [0 0]))))

(defn create-marker [img]
  (js/mapboxgl.Marker. img))

(defn on-load [map f]
  (.on map "load" f))

(defn remove-square [map id]
  (if (.getLayer map id) (.removeLayer map id))
  (if (.getSource map id) (.removeSource map id)))

(defn fit-bounds [map coordinates]
  (.fitBounds map (clj->js coordinates) (clj->js {:padding 150})))

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

(defn add-marker [map coordinate img-src scale]
  (let [img (js/Image.)]
    (set! (.-onload img)
      (fn []
        (let [w (.-width img)
              h (.-height img)]
          (set! (.-width img) (* w scale))
          (set! (.-height img) (* h scale)))))
    (set! (.-src img) img-src)
    (let [marker (create-marker img)]
      (.setLngLat marker (clj->js coordinate))
      (.addTo marker map)
      (swap! markers conj marker))))

(defn clear-markers []
  (doseq [marker @markers]
    (.remove marker))
  (reset! markers []))

(defn set-square [map square]
  (remove-square map "outer")
  (remove-square map "inner")
  (add-square map "outer" 3 (:OuterCoordinates square))
  (add-square map "inner" 2 (:InnerCoordinates square))
  (clear-markers)
  (add-marker map (:Center square) (:LabelUrl square) 0.75)
  ;;TODO: sub labels!
  (fit-bounds map (:Bounds square)))

(defn set-all-squares [map square]
  (remove-square map "outer")
  (remove-square map "inner")
  (add-square map "inner" 2 (:OuterCoordinates square))
  (.setZoom map 2))

