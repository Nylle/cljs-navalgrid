(ns navalgrid.web.maplibre
  (:require ["maplibre-gl" :as maplibregl]))

(defonce map-inst (atom nil))
(defonce markers (atom nil))

(defn create! [ref props f]
  (when-let [el @ref]
    (let [^js m (maplibregl/Map. (clj->js (assoc props :container el)))]
      (.addControl m (maplibregl/NavigationControl.))
      (.on m "load" f)
      (.on m "load" (fn [] (.setCenter m (clj->js (.getCenter m)))))
      (reset! map-inst m))))

(defn destroy! []
  (when-let [^js m @map-inst]
    (.remove m)
    (reset! map-inst nil)))

(defn add-source! [id geojson]
  (when-let [^js m @map-inst]
    (.addSource m id (clj->js geojson))
    (reset! map-inst m)))

(defn remove-source! [id]
  (when-let [^js m @map-inst]
    (when (.getSource m id) (.removeSource m id))
    (reset! map-inst m)))

(defn add-layer! [layer]
  (when-let [^js m @map-inst]
    (.addLayer m (clj->js layer))
    (reset! map-inst m)))

(defn remove-layer! [id]
  (when-let [^js m @map-inst]
    (when (.getLayer m id) (.removeLayer m id))
    (reset! map-inst m)))

(defn fit-bounds! [[sw-lnglat ne-lnglat]]
  (when-let [^js m @map-inst]
    (.fitBounds m (clj->js [sw-lnglat ne-lnglat]) (clj->js {:padding 50}))
    (reset! map-inst m)))

(defn add-marker! [text lnglat class]
  (when-let [^js m @map-inst]
    (let [div (js/document.createElement "div")
          _ (set! (.-className div) class)
          _ (set! (.-textContent div) text)]
      (let [marker (-> (maplibregl/Marker. #js {:element div})
                       (.setLngLat (clj->js lnglat))
                       (.addTo m))]
        (swap! markers assoc text marker)))
      (reset! map-inst m)))

(defn clear-markers! []
  (doseq [[_ marker] @markers]
    (.remove marker))
  (reset! markers {}))