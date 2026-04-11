(ns navalgrid.web.home.model
  (:require [clojure.string :as str]
            [navalgrid.domain.square :as square]
            [navalgrid.persistence.repository :as repo]))

(defn str->ref [s]
  (when s (-> (str/upper-case s)
              (str/replace #"[^ÄA-Z0-9]" "")
              (subs 0 6))))

(defn with-sub-squares [ref]
  (when-let [square (repo/find-by-id ref)]
    (assoc square :sub-squares (-> (square/sub-square-refs (:id square) (:so square))
                                   (repo/find-all-by-ids))
                  :center (square/center-coord square))))

(defn region [ref]
  (when ref (repo/find-region ref)))

(defn format-scale [n]
  (->> (long n)
       str
       reverse
       (partition-all 3)
       (map #(apply str (reverse %)))
       reverse
       (str/join " ")))