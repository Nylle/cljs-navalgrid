(ns navalgrid.map.maplibre
  (:require ["maplibre-gl" :as maplibregl]
            [navalgrid.math :as math]))

(defonce map-inst (atom nil))
(defonce markers (atom nil))
(defonce source-ids (atom #{}))
(defonce layer-ids (atom #{}))

(defn create! [ref props loaded-fn moved-fn]
  (when-let [el @ref]
    (let [^js m (maplibregl/Map. (clj->js (assoc props :container el)))]
      (.addControl m (maplibregl/NavigationControl.))
      (.on m "load" loaded-fn)
      (.on m "moveend" moved-fn)
      (reset! map-inst m))))

(defn destroy! []
  (when-let [^js m @map-inst]
    (.remove m)
    (reset! map-inst nil)))

(defn get-center []
  (when-let [^js m @map-inst]
    (let [c (.getCenter m)]
      [(.-lng c) (.-lat c)])))

(defn set-center! [lnglat]
  (when-let [^js m @map-inst]
    (.setCenter m (clj->js lnglat))
    (reset! map-inst m)))

(defn get-zoom []
  (when-let [^js m @map-inst]
    (.getZoom m)))

(defn set-zoom! [zoom]
  (when-let [^js m @map-inst]
    (.setZoom m zoom)
    (reset! map-inst m)))

(defn add-source! [id geojson]
  (when-let [^js m @map-inst]
    (.addSource m id (clj->js geojson))
    (swap! source-ids conj id)
    (reset! map-inst m)))

(defn remove-source! [id]
  (when-let [^js m @map-inst]
    (swap! source-ids disj id)
    (when (.getSource m id) (.removeSource m id))
    (reset! map-inst m)))

(defn remove-sources! [prefix]
  (doseq [sid (->> @source-ids (filter #(-> % (.startsWith prefix))))]
    (remove-source! sid)))

(defn add-layer! [layer]
  (when-let [^js m @map-inst]
    (.addLayer m (clj->js layer))
    (swap! layer-ids conj (:id layer))
    (reset! map-inst m)))

(defn remove-layer! [id]
  (when-let [^js m @map-inst]
    (swap! layer-ids disj id)
    (when (.getLayer m id) (.removeLayer m id))
    (reset! map-inst m)))

(defn remove-layers! [prefix]
  (doseq [lid (->> @layer-ids (filter #(-> % (.startsWith prefix))))]
    (remove-layer! lid)))

(defn hide-layer! [id]
  (when-let [^js m @map-inst]
    (when (.getLayer m id) (.setLayoutProperty m id "visibility" "none"))
    (reset! map-inst m)))

(defn show-layer! [id]
  (when-let [^js m @map-inst]
    (when (.getLayer m id) (.setLayoutProperty m id "visibility" "visible"))
    (reset! map-inst m)))

(defn fit-bounds! [[sw-lnglat ne-lnglat]]
  (when-let [^js m @map-inst]
    (.fitBounds m (clj->js [sw-lnglat ne-lnglat]) (clj->js {:padding 50}))
    (reset! map-inst m)))

(defn create-marker [text lnglat class offset]
  (let [[x y] (or offset [0 0])
        div (js/document.createElement "div")
        _ (set! (.-className div) class)
        _ (set! (.-textContent div) text)]
    (-> (maplibregl/Marker. #js {:element div :offset #js [x y]})
        (.setLngLat (clj->js lnglat)))))

(defn add-marker! [id ^js marker]
  (when-let [^js m @map-inst]
    (.addTo marker m)
    (swap! markers assoc id marker)
    (reset! map-inst m)))

(defn clear-markers! []
  (doseq [[_ marker] @markers]
    (.remove marker))
  (reset! markers {}))

(defn meters-per-pixel [lat zoom]
  (let [R 40075016.686
        lat' (math/to-radians lat)]
    (-> (/ R 256)
        (* (math/cos lat'))
        (/ (math/pow 2 zoom)))))

(defn get-scale-denominator [lat zoom]
  (let [dpi 96
        mpi 0.0254]
    (-> (meters-per-pixel lat zoom)
        (* dpi)
        (/ mpi))))