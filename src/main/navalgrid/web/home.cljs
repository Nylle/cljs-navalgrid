(ns navalgrid.web.home
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [navalgrid.web.model :as model]
            [navalgrid.web.maps :as m]))

(rf/reg-fx :run-do (fn [f] (f)))
(rf/reg-event-fx :query-change (fn [{:keys [db]} [_ s]]
                                 (let [ref (model/str->ref s)
                                       square (model/with-sub-squares ref)]
                                   {:db     (assoc db :query ref :result square)
                                    :run-do #(m/set-square! square)})))

(rf/reg-sub :query (fn [db _] (:query db)))
(rf/reg-sub :result (fn [db _] (:result db)))

(defn coord [x]
  (str (first x) ", " (second x)))

(defn query-input []
  [:input {:type      "text"
           :value     @(rf/subscribe [:query])
           :on-change #(rf/dispatch [:query-change (-> % .-target .-value)])}])

(defn regular [square]
  [:dl
   [:dt "NW"] [:dd (coord (:nw square))]
   [:dt "NE"] [:dd (coord [(first (:nw square)) (second (:se square))])]
   [:dt "SE"] [:dd (coord (:se square))]
   [:dt "SW"] [:dd (coord [(first (:se square)) (second (:nw square))])]])

(defn poly [square]
  (let [letters (cons "NW" (map char (range 98 123)))]
    (into [:dl] (mapcat (fn [a b] [[:dt a] [:dd (coord b)]]) letters (:poly square)))))

(defn output []
  (let [res @(rf/subscribe [:result])]
    (cond
      (= nil res) [:div ""]
      (:poly res) [poly res]
      :default [regular res])))

(defn map-view [parent]
  [:div {:id    "map"
         :ref   (fn [el] (reset! parent el))
         :style {:width "100%" :height "100vh"}}])

(defn canvas []
  (let [this (r/atom nil)]
    (r/create-class
      {:display-name           "canvas"
       :component-did-mount    (m/create-fn this)
       :component-will-unmount (m/destroy-fn)
       :reagent-render         (fn [] [map-view this])})))

(defn init []
  [:<>
   [:aside
    [:h1 "navalgrid"]
    [query-input]
    [output]]
   [:main
    [canvas]]])