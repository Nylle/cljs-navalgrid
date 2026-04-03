(ns navalgrid.persistence.repository
  (:require [navalgrid.domain.square :as square]
            [navalgrid.persistence.data :as data]
            [navalgrid.persistence.regions :as regions]
            [navalgrid.utils :as utils]))

(defn extract-from-group [ref group]
  (let [{:keys [ids nw se poly o] :or {o :h}} group]
    (if poly
      (-> (merge group {:id ref :poly poly})
          (dissoc :ids))
      (let [i (.indexOf ids ref)]
        (when (> i -1)
          (-> (merge group (square/shift {:id ref :nw nw :se se} o i))
              (dissoc :ids :o)))))))

(defn find-large [ref]
  (let [large (apply str (take 2 ref))]
    (->> (concat data/large-regular-squares data/large-partial-squares)
         (filter #(utils/seq-contains? (:ids %) large))
         (map #(extract-from-group large %))
         (map #(square/from-square-def ref %)))))

(defn find-irregular [ref]
  (->> (concat data/irregular-squares data/polygonal-squares)
       (filter #(utils/seq-contains? (:ids %) ref))
       (map #(extract-from-group ref %))))

(defn two-by-five-search-key [ref]
  (when-let [subs (seq (drop 2 ref))]
    (let [large (apply str (take 2 ref))]
      (if (and (= "0" (first subs)) (> (count subs) 1))
        (str large (first (drop 1 subs)))
        (str large (first subs))))))

(defn find-two-by-five [ref]
  (let [key (two-by-five-search-key ref)]
    (->> (concat data/two-by-five-squares)
         (filter #(utils/seq-contains? (:ids %) key))
         (map #(extract-from-group key %))
         (map #(square/from-square-def ref %)))))

(defn find-partial [ref]
  (let [large (apply str (take 2 ref))
        subs (apply str (drop 2 ref))
        refs (drop 1 (reductions str large subs))]
    (->> data/partial-squares
         (mapcat #(map (fn [r] {:ref r :square %}) (filter (set (:ids %)) refs)))
         (map #(extract-from-group (:ref %) (:square %)))
         (map #(square/from-square-def ref %)))))

(defn find-by-id [ref]
  (->> (concat (find-large ref) (find-irregular ref) (find-two-by-five ref) (find-partial ref))
       (take 1)
       (first)))

(defn find-all-by-ids [refs]
  (->> (map #(find-by-id %) refs)
       (remove nil?)))

(defn extract-all [group filter-fn]
  (let [ids (filter filter-fn (:ids group))]
    (map #(extract-from-group % group) ids)))

(defn all-large-squares []
  (let [filter-fn #(= 2 (count %))]
    (->> (concat data/large-regular-squares data/large-partial-squares data/irregular-squares data/polygonal-squares)
         (mapcat #(extract-all % filter-fn)))))

(defn find-region [ref]
  (let [large (apply str (take 2 ref))]
    (->> regions/all
         (filter #(utils/seq-contains? (:ids %) large))
         (first)
         (:name))))