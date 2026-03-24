(ns navalgrid.web.maplibre
  (:require ["maplibre-gl" :as maplibregl]))

(defonce map-inst (atom nil))

(defn create! [ref]
  (when-let [el @ref]
    (let [m (maplibregl/Map. (clj->js {:container el
                                       :style     "https://demotiles.maplibre.org/style.json"
                                       :center    [0 0]
                                       :zoom      0}))]
      (.addControl m (maplibregl/NavigationControl.))
      (reset! map-inst m))))

(defn destroy! []
  (when-let [m @map-inst]
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
    (.fitBounds m (clj->js [sw-lnglat ne-lnglat]) (clj->js {:padding 100}))
    (reset! map-inst m)))