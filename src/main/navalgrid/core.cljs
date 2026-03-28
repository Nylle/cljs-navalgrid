(ns navalgrid.core
  (:require [reagent.dom.client :as rdc]
            [re-frame.core :as rf]
            [navalgrid.web.home.view :as home]))

(defonce root-container (rdc/create-root (js/document.getElementById "root")))

(defn mount-ui []
  (rdc/render root-container [home/init]))

(defn ^:dev/after-load clear-cache-and-render! []
  (rf/clear-subscription-cache!)
  (mount-ui))

(defn init []
  (mount-ui))