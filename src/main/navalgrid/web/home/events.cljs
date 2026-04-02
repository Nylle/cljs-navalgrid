(ns navalgrid.web.home.events
  (:require [navalgrid.web.router :as router]
            [navalgrid.web.home.model :as model]
            [navalgrid.web.maps :as m]))

(defn init-db [db _]
  (let [ref (-> (router/get-square-ref-from-url)
                (model/str->ref))]
    (assoc db :query ref :square (model/with-sub-squares ref))))

(defn query-changed-fx [{:keys [db]} [_ query]]
  (let [ref (model/str->ref query)
        square (model/with-sub-squares ref)]
    {:db     (assoc db :query ref :square square)
     :run-do (fn []
               (m/set-square! square)
               (router/set-square-url! square))}))

(defn map-loaded-fx [{:keys [db]} _]
  (let [square (:square db)]
    {:run-do (fn [] (m/set-square! square))}))

(defn map-moved-db [db _]
  (let [scale (m/scale-denominator)]
    (assoc db :scale scale)))