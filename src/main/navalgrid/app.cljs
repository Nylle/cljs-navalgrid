(ns navalgrid.app
  (:require [reagent.dom.client :as rdc]
            [re-frame.core :as rf]
            [navalgrid.web.main :refer [index]]))

(defonce root-container (rdc/create-root (js/document.getElementById "root")))

(defn mount-ui []
  (rdc/render root-container [index]))

(defn ^:dev/after-load clear-cache-and-render! []
  (rf/clear-subscription-cache!)
  (mount-ui))

(defn run []
  (mount-ui))