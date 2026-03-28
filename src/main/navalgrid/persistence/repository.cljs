(ns navalgrid.persistence.repository
  (:require [navalgrid.domain.square :as square]
            [navalgrid.persistence.data :as data]
            [navalgrid.core :as core]))

(defn extract-from-group [ref group]
  (let [{:keys [ids nw se o] :or {o :h}} group
        i (.indexOf ids ref)]
    (if (> i -1)
      (-> (merge group (square/shift {:id ref :nw nw :se se} o i))
          (dissoc :ids :o))
      nil)))

(defn find-large [ref]
  (let [large (apply str (take 2 ref))]
    (->> (concat data/large-regular-squares data/large-partial-squares)
         (filter #(core/seq-contains? (:ids %) large))
         (map #(extract-from-group large %))
         (map #(square/from-square-def ref %)))))

(defn find-irregular [ref]
  (->> data/irregular-squares
       (filter #(core/seq-contains? (:ids %) ref))
       (map #(extract-from-group ref %))))

(defn find-polygonal [ref]
  (->> data/polygonal-squares
       (filter #(= (:id %) ref))
       (map (fn [x] {:id ref :poly (:poly x)}))))

(defn two-by-five-search-key [ref]
  (if-let [subs (seq (drop 2 ref))]
    (let [large (apply str (take 2 ref))]
      (if (and (= "0" (first subs)) (> (count subs) 1))
        (str large (first (drop 1 subs)))
        (str large (first subs))))))

(defn find-two-by-five [ref]
  (let [key (two-by-five-search-key ref)]
    (->> (concat data/two-by-five-squares)
         (filter #(core/seq-contains? (:ids %) key))
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
  (->> (concat (find-large ref) (find-irregular ref) (find-polygonal ref) (find-two-by-five ref) (find-partial ref))
       (take 1)
       (first)))

(defn find-all-by-ids [refs]
  (->> (map #(find-by-id %) refs)
       (remove nil?)))