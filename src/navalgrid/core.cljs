(ns ^:figwheel-hooks navalgrid.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
    [cljs.core.async :refer [>!]]
    [reagent.core :as r]
    [navalgrid.mapbox :as m]
    [navalgrid.api :as api]))

(defonce s (r/atom {:id "bf"}))

(defn map-refresh [id]
  (let [map (:map @s)]
    (swap! s assoc :id id)
    (go
     (let [square (:body (<! (api/get-square id)))]
       (m/set-square map square)
       (swap! s assoc :map map)))))

(defn map-canvas []
  (r/create-class
   {:display-name   "map-canvas"
    :reagent-render (fn [] [:div#canvas])
    :component-did-mount
    (fn [comp]
      (go
       (let [square (:body (<! (api/get-square "bf3")))]
         (let [map (m/create-map comp (:Center square))]
           (m/on-load map
                      (fn []
                        (m/set-square map square)
                        (swap! s assoc :map map)))))))}))

(defn menu []
  [:div#menu
   [:input
    {:type      :text
     :name      :square-id
     :value     (:id @s)
     :on-change (fn [e] (map-refresh (-> e .-target .-value)))}]])

(defn app []
  [:div#app
   [menu]
   [map-canvas]])

(defn mount []
  (reagent.dom/render [app] (js/document.getElementById "root")))

(defn ^:after-load re-render []
  (mount))

(defonce start-up (do (mount) true))