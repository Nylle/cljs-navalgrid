(ns navalgrid.web.home.events
  (:require [navalgrid.web.router :as router]
            [navalgrid.web.home.model :as model]
            [navalgrid.web.maps :as m]))

(defn init-db [db _]
  (let [ref (-> (router/get-square-ref-from-url)
                (model/str->ref))]
    (assoc db :query ref :square (model/with-sub-squares ref))))

(defn query-changed-fx [{:keys [db]} [_ s]]
  (let [ref (model/str->ref s)
        square (model/with-sub-squares ref)]
    {:db     (assoc db :query ref :square square)
     :run-do (fn []
               (m/set-square! square)
               (router/set-square-url! square))}))

(defn map-loaded-fx [{:keys [db]} _]
  (let [square (:square db)]
    {:run-do (fn [] (m/set-square! square))}))