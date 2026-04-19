(ns navalgrid.web.home.model
  (:require [clojure.string :as str]
            [navalgrid.domain.square :as square]
            [navalgrid.map.core :as m]
            [navalgrid.map.polylabel :as p]
            [navalgrid.persistence.repository :as repo]))

(defn str->ref [s]
  (when s (-> (str/upper-case s)
              (str/replace #"[^ÄA-Z0-9]" "")
              (subs 0 6))))

(defn find-square [ref]
  (when-let [square (repo/find-by-id ref)]
    (assoc square :sub-squares (-> (square/sub-square-refs (:id square) (:so square))
                                   (repo/find-all-by-ids))
                  :label (-> [(m/square->polygon square)]
                             (p/pole-of-inaccessability)
                             (m/coord<->lngLat))
                  :center (square/center-coord square)
                  :dimensions (square/dimensions square))))

(defn region [ref]
  (if ref
    (repo/find-region ref)
    {:label "Weltkarte"}))

(defn format-scale [n]
  (->> (long n)
       str
       reverse
       (partition-all 3)
       (map #(apply str (reverse %)))
       reverse
       (str/join " ")))