(ns navalgrid.web.main
  (:require [clojure.string :as str]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [navalgrid.persistence.repository :as repo]))

(def letters
  (map char (range 65 91)))

(defn str->ref [s]
  (-> (str/upper-case s)
      (str/replace #"[^ÄA-Z0-9]" "")
      (subs 0 6)))



(rf/reg-event-db :query-change (fn [db [_ s]]
                                 (let [ref (str->ref s)]
                                   (assoc db :query ref
                                             :result (repo/find-by-id ref)))))

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

(defn canvas []
  [:div#canvas])

(defn index []
  [:<>
   [:aside
    [:h1 "navalgrid"]
    [query-input]
    [output]]
   [:main
    [canvas]]])