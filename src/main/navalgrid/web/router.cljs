(ns navalgrid.web.router
  (:require [clojure.string :as str]
            [re-frame.core :as rf]))

(defn path->segments [path]
  (->> (str/split path #"/")
       (remove str/blank?)
       (map js/decodeURIComponent)
       vec))

(defn segments->path [segments]
  (->> (map js/encodeURIComponent segments)
       (map #(str/replace-all % #"%2C" ","))
       (remove str/blank?)
       (str/join "/")
       (str "/")))

(defn set-path! [segments]
  (let [cur (.-pathname js/window.location)
        p (segments->path segments)]
    (when (not= cur p)
      (.pushState js/history nil "" p))))

(defn get-path []
  (path->segments (.-pathname js/window.location)))

(defn get-ref-from-url []
  (let [path (get-path)]
    (when (= "square" (first path))
      (second path))))

(defn get-coords-from-url []
  (let [path (get-path)]
    (when (= "coords" (first path))
      (second path))))

(defn set-query-url! [square coords]
  (cond
    (seq coords) (set-path! ["coords" (str (first coords) "," (second coords))])
    (not (nil? square)) (set-path! ["square" (:id square)])
    :default (set-path! [])))

(defn init! []
  (.addEventListener js/window "popstate" (fn [_] (rf/dispatch [:route/changed (get-path)]))))