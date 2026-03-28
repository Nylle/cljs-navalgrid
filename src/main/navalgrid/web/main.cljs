(ns navalgrid.web.main
  (:require [clojure.string :as str]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [navalgrid.web.model :as model]
            [navalgrid.web.map :as m]))

(def letters
  (cons "NW" (map char (range 98 123))))

(defn str->ref [s]
  (-> (str/upper-case s)
      (str/replace #"[^ÄA-Z0-9]" "")
      (subs 0 6)))


(rf/reg-fx :run-do (fn [f] (f)))

(rf/reg-event-fx :query-change (fn [{:keys [db]} [_ s]]
                                 (let [ref (str->ref s)
                                       square (model/with-sub-squares ref)]
                                   {:db     (assoc db :query ref :result square)
                                    :run-do #(m/set-square! square)})))

(rf/reg-sub :query (fn [db _] (:query db)))
(rf/reg-sub :result (fn [db _] (:result db)))



(defn query-input []
  [:input {:type      "text"
           :value     @(rf/subscribe [:query])
           :on-change #(rf/dispatch [:query-change (-> % .-target .-value)])}])

(defn coord [x]
  (str (first x) ", " (second x)))

(defn regular [square]
  [:dl
   [:dt "NW"] [:dd (coord (:nw square))]
   [:dt "NE"] [:dd (coord [(first (:nw square)) (second (:se square))])]
   [:dt "SE"] [:dd (coord (:se square))]
   [:dt "SW"] [:dd (coord [(first (:se square)) (second (:nw square))])]])

(defn poly [square]
  (into [:dl] (mapcat (fn [a b] [[:dt a] [:dd (coord b)]]) letters (:poly square))))

(defn output []
  (let [res @(rf/subscribe [:result])]
    (cond
      (= nil res) [:div ""]
      (:poly res) [poly res]
      :default [regular res])))

(defn map-component [ref]
  [:div {:id    "map"
         :ref   (fn [el] (reset! ref el))
         :style {:width "100%" :height "100vh"}}])

(defn canvas []
  (let [ref (r/atom nil)]
    (r/create-class
      {:display-name           "canvas"
       :component-did-mount    (m/create-fn ref)
       :component-will-unmount (m/destroy-fn)
       :reagent-render         (fn [] [map-component ref])})))

(defn index []
  [:<>
   [:aside
    [:h1 "navalgrid"]
    [query-input]
    [output]]
   [:main
    [canvas]]])