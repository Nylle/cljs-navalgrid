(ns navalgrid.web.modal
  (:require [re-frame.core :as rf]))

(rf/reg-event-db :modal/open (fn [db [_ modal-map]] (assoc db :modal modal-map)))
(rf/reg-event-db :modal/close (fn [db _] (assoc db :modal nil)))

(rf/reg-sub :modal (fn [db _] (:modal db)))

(defn modal []
  (let [m (rf/subscribe [:modal])]
    (fn []
      (when-let [m @m]
        (let [{:keys [title body]} m]
          [:div.modal-overlay {:on-click #(rf/dispatch [:modal/close])}
           [:div.modal-content {:on-click (fn [e] (.stopPropagation e))}
            [:div.modal-header
             [:h3 title]]
            [:div.modal-body body]
            [:div.modal-footer
             [:button {:on-click #(rf/dispatch [:modal/close])} "Done"]]]])))))