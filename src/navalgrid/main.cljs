(ns ^:figwheel-hooks navalgrid.main
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
    [reagent.core :as r]
    [cljs-http.client :as http]
    [cljs.core.async :refer [<!]]
    [navalgrid.mapbox :as m]))

(defn map-canvas-render []
  [:div#canvas])

(defn map-canvas-did-mount [comp]
  (let [map (m/create-map comp [-9.25 49.65])]
    (m/on-load map (fn []
           (m/add-square map "outer" 3 [[-11.5 51] [-7 51] [-7 48.3] [-11.5 48.3] [-11.5 51]])
           (m/fit-bounds map [[-11.50 48.30] [-7.00 51.00]])))))

(defn map-canvas []
  (r/create-class
   {:display-name         "map-canvas"
    :reagent-render       map-canvas-render
    :component-did-mount  map-canvas-did-mount
    :component-did-update map-canvas-did-mount}))

(defn app []
  [map-canvas])


(defn mount []
  (reagent.dom/render [app] (js/document.getElementById "root")))


(defn ^:after-load re-render []
  (mount))


(defonce start-up (do (mount) true))