(ns navalgrid.web.model
  (:require [navalgrid.domain.square :as square]
            [navalgrid.persistence.repository :as repo]))

(defn with-sub-squares [ref]
  (when-let [square (repo/find-by-id ref)]
    (assoc square :sub-squares (-> (square/sub-square-refs (:id square) (:so square))
                                   (repo/find-all-by-ids)))))