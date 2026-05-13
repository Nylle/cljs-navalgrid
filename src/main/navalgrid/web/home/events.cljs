(ns navalgrid.web.home.events
  (:require [navalgrid.web.router :as router]
            [navalgrid.web.storage :as storage]
            [navalgrid.web.home.model :as model]
            [navalgrid.map.core :as m]
            [re-frame.core :as rf]))

(rf/reg-fx :run-do (fn [f] (f)))

(rf/reg-event-db
  :init
  (fn [db _]
    (let [scale (m/scale-denominator)
          ref (-> (router/get-square-ref-from-url) (model/str->ref))
          square (model/ref->square ref)
          region (model/region (:id square))
          format (some-> (storage/ls-get :format) (as-> v (if (string? v) (keyword v) v)))]
      (assoc db :query ref :scale scale :square square :region region :modal nil :format (or format :dms)))))

(rf/reg-event-fx
  :query/changed
  (fn [{:keys [db]} [_ query]]
    (let [ref (model/str->ref query)
          square (model/ref->square ref)
          region (model/region (:id square))]
      {:db     (assoc db :query ref :square square :region region)
       :run-do (fn []
                 (m/set-square! square)
                 (router/set-square-url! square))})))

(rf/reg-event-fx
  :map/loaded
  (fn [{:keys [db]} _]
    (let [square (:square db)]
      {:run-do (fn []
                 (m/draw-all-large-squares!)
                 (m/set-square! square))})))

(rf/reg-event-db
  :map/moved
  (fn [db _]
    (let [scale (m/scale-denominator)]
      (assoc db :scale scale))))