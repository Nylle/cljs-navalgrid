(ns ^:figwheel-hooks navalgrid.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
    [cljs.core.async :refer [>!]]
    [reagent.core :as r]
    [navalgrid.mapbox :as m]
    [navalgrid.api :as api]))

(defonce s (r/atom {}))

(defn error-msg [status]
  (case status
        400 "Invalid square reference"
        404 "Square not found"
        (str "Error: " status)))

(defn map-refresh [id]
  (swap! s assoc :id id)
  (if (> (count id) 1)
    (let [map (:map @s)]
      (go
       (let [response (<! (api/get-square id))]
         (if (not= (:status response) 200)
           (swap! s assoc :search-message (error-msg (:status response)))
           (let [square (:body response)]
             (m/set-square map square)
             (swap! s assoc :map map)
             (swap! s assoc :search-message ""))))))))

(defn map-canvas []
  (r/create-class
   {:display-name   "map-canvas"
    :reagent-render (fn [] [:div#canvas])
    :component-did-mount
    (fn [comp]
      (go
       (let [map (m/create-map comp [0 40])]
         (m/on-load map
                    (fn []
                      (swap! s assoc :map map))))))}))

(defn menu []
  [:div#menu
   [:input
    {:type      :text
     :name      :square-id
     :value     (:id @s)
     :on-change (fn [e] (map-refresh (-> e .-target .-value)))}]
   [:div#search-message (:search-message @s)]
   [:div#square-info]])

(defn app []
  [:div#app
   [menu]
   [map-canvas]])

(defn mount []
  (reagent.dom/render [app] (js/document.getElementById "root")))

(defn ^:after-load re-render []
  (mount))

(defonce start-up (do (mount) true))