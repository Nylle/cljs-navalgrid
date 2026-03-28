(ns navalgrid.web.router
  (:require [clojure.string :as str]))

(defn- path->segments [path]
  (->> (str/split path #"/")
       (remove str/blank?)
       (map js/decodeURIComponent)
       vec))

(defn- segments->path [segments]
  (->> (map js/encodeURIComponent segments)
       (remove str/blank?)
       (str/join "/")
       (str "/")))

(defn set-path! [segments]
  (let [cur (.-pathname js/window.location)
        p (segments->path segments)]
    (when (not= cur p)
      (.replaceState js/history nil "" p))))

(defn get-path []
  (path->segments (.-pathname js/window.location)))

(defn get-square-ref-from-url []
  (let [path (get-path)]
    (when (= "square" (first path))
      (second path))))

(defn set-square-url! [square]
  (if square
    (set-path! ["square" (:id square)])
    (set-path! [])))

