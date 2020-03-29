(ns ^:figwheel-hooks navalgrid.main
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
    [reagent.core :as r]
    [cljs-http.client :as http]
    [cljs.core.async :refer [<!]]))

(defn map-canvas-render []
  [:div#canvas])

(defn map-canvas-did-mount [comp]
  (println "I mounted")
  (let [map (js/createMap "canvas" 4 #js [-9.25 49.65])]
    (.on map "load"
         (fn []
           (.addLayer map
                      (clj->js
                       {:id     "outer",
                        :type   "line",
                        :source
                        {:type "geojson",
                         :data
                         {:type "Feature",
                          :geometry
                          {:type "LineString",
                           :coordinates
                           [[-11.5 51] [-7 51] [-7 48.3] [-11.5 48.3] [-11.5 51]]}}},
                        :layout {:line-cap "square"},
                        :paint  {:line-color "#038D3C", :line-width 3}}))))))

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