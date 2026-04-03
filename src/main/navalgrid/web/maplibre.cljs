(ns navalgrid.web.maplibre
  (:require ["maplibre-gl" :as maplibregl]
            [navalgrid.math :as math]))

(defonce map-inst (atom nil))
(defonce markers (atom nil))

(defn create! [ref props loaded-fn moved-fn]
  (when-let [el @ref]
    (let [^js m (maplibregl/Map. (clj->js (assoc props :container el)))]
      (.addControl m (maplibregl/NavigationControl.))
      (.on m "load" loaded-fn)
      (.on m "load" (fn [] (.setCenter m (clj->js (.getCenter m)))))
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