(ns navalgrid.web.home.events
  (:require [navalgrid.web.router :as router]
            [navalgrid.web.storage :as storage]
            [navalgrid.web.home.model :as model]
            [navalgrid.map.core :as m]
            [navalgrid.domain.coords :as coords]
            [re-frame.core :as rf]))

(rf/reg-fx :run-do (fn [f] (f)))

(rf/reg-event-db
  :init
  (fn [db _]
    (let [scale (m/scale-denominator)
          ref (-> (router/get-ref-from-url) (model/str->ref))
          coords (-> (router/get-coords-from-url) (coords/str->coords))
          squares (model/square ref coords)
          region (model/region (:id (first squares)))
          format (or (some-> (storage/ls-get :format) (as-> v (if (string? v) (keyword v) v))) :dms)
          query (or ref (coords/coords->str coords format))]
      (assoc db :query query :scale scale :square squares :location coords :region region :modal nil :format format))))

(rf/reg-event-fx
  :query/changed
  (fn [{:keys [db]} [_ q]]
    (let [ref (model/str->ref q)
          coords (coords/str->coords q)
          squares (model/square ref coords)
          region (model/region (:id (first squares)))
          query (or ref q (coords/coords->str coords (:format db)))]
      {:db     (assoc db :query query :square squares :region region :location coords)
       :run-do (fn []
                 (m/set-squares! squares coords)
                 (router/set-query-url! (first squares) coords))})))

(rf/reg-event-fx
  :map/loaded
  (fn [{:keys [db]} _]
    (let [squares (:square db)
          coords (:location db)]
      {:run-do (fn []
                 (m/draw-all-large-squares!)
                 (m/set-squares! squares coords))})))

(rf/reg-event-db
  :map/moved
  (fn [db _]
    (let [scale (m/scale-denominator)]
      (assoc db :scale scale))))