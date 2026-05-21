(ns navalgrid.web.home.model
  (:require [clojure.string :as str]
            [navalgrid.domain.square :as square]
            [navalgrid.domain.coords :as coords]
            [navalgrid.map.core :as m]
            [navalgrid.map.polylabel :as p]
            [navalgrid.persistence.repository :as repo]))

(defn ref? [s]
  (when-let [c (first s)]
    (boolean (re-matches #"[ÄaA-Za-z].*" (str c)))))

(defn str->ref [s]
  (when (ref? s)
    (-> (str/upper-case s)
        (str/replace #"[^ÄA-Z0-9]" "")
        (subs 0 6))))

(defn str->coords [s]
  (when (not (ref? s))
    (coords/str->coords s)))

(defn explode [square]
  (->> (square/sub-square-refs (:id square) (:so square))
       (repo/find-all-by-ids)))

(defn explode-matching* [square coords]
  (loop [squares [square]
         level 0]
    (let [hits (filter #(square/contains-coords? % coords) squares)]
      (if (or (= level 4) (empty? hits))
        hits
        (recur (mapcat explode hits) (inc level))))))

(defn enrich [square]
  (when square
    (-> square
        (assoc :sub-squares (->> (explode square)
                                 (map #(dissoc % :sub)))
               :label (-> [(m/square->polygon square)]
                          (p/pole-of-inaccessability)
                          (m/coord<->lngLat))
               :center (square/center-coord square)
               :dimensions (square/dimensions square))
        (dissoc :sub))))

(defn square [ref coords]
  (map enrich
       (if ref
         (when-let [res (repo/find-by-id ref)]
           [res])
         (when (and coords (> (count coords) 1))
           (->> (repo/all-large-squares)
                (mapcat #(explode-matching* % coords)))))))

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