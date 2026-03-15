(ns navalgrid.persistence.repository
  (:require [navalgrid.domain.square :as square]
            [navalgrid.persistence.data :as data]
            [navalgrid.core :as core]))

(defn extract-from-group [id group]
  (let [{:keys [ids nw se o] :or {o :h}} group
        i (.indexOf ids id)]
    (if (> i -1)
      (-> (merge group (square/shift {:id id :nw nw :se se} o i))
          (dissoc :ids :o))
      nil)))

(defn find-large [id]
  (let [large (apply str (take 2 id))]
    (->> (concat data/large-regular-squares data/large-partial-squares)
         (filter #(core/seq-contains? (:ids %) large))
         (map #(extract-from-group large %)))))

(defn find-irregular [id]
  (->> (concat data/irregular-squares data/two-by-five-squares)
       (filter #(core/seq-contains? (:ids %) id))
       (map #(extract-from-group id %))))

(defn find-polygonal [id]
  (->> data/polygonal-squares
       (filter #(= (:id %) id))
       (map (fn [x] {:id id :poly (:poly x)}))))

(defn find-partial [id]
  (let [large (apply str (take 2 id))
        subs (apply str (drop 2 id))
        ids (set (drop 1 (reductions str large subs)))]
    (->> data/partial-squares
         (filter #(some ids (:ids %)))
         (map #(extract-from-group id %)))))

(defn find-by-id [id]
  (->> (concat (find-large id) (find-irregular id) (find-polygonal id) (find-partial id))
       (take 1)))