(ns navalgrid.web.home.model
  (:require [clojure.string :as str]
            [navalgrid.domain.square :as square]
            [navalgrid.persistence.repository :as repo]))

(defn str->ref [s]
  (-> (str/upper-case s)
      (str/replace #"[^ÄA-Z0-9]" "")
      (subs 0 6)))

(defn with-sub-squares [ref]
  (when-let [square (repo/find-by-id ref)]
    (assoc square :sub-squares (-> (square/sub-square-refs (:id square) (:so square))
                                   (repo/find-all-by-ids)))))