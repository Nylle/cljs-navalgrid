(ns navalgrid.web.home.view
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [navalgrid.web.maps :as m]
            [navalgrid.web.home.events :as e]
            [navalgrid.web.home.model :as model]))

(rf/reg-fx :run-do (fn [f] (f)))
(rf/reg-event-db :init e/init-db)
(rf/reg-event-fx :query/changed e/query-changed-fx)
(rf/reg-event-fx :map/loaded e/map-loaded-fx)
(rf/reg-event-db :map/moved e/map-moved-db)

(rf/reg-sub :query (fn [db _] (:query db)))
(rf/reg-sub :square (fn [db _] (:square db)))
(rf/reg-sub :scale (fn [db _] (:scale db)))

(defn coord [x]
  (str (first x) ", " (second x)))

(defn query-input []
  [:input {:type      "text"
           :value     @(rf/subscribe [:query])
           :on-change #(rf/dispatch [:query/changed (-> % .-target .-value)])}])

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
  (let [res @(rf/subscribe [:square])]
    (cond
      (= nil res) [:div ""]
      (:poly res) [poly res]
      :default [regular res])))

(defn map-view [parent]
  [:div {:id  "map"
         :ref (fn [el] (reset! parent el))}])

(defn canvas []
  (let [this (r/atom nil)]
    (r/create-class
      {:display-name           "canvas"
       :component-did-mount    (m/create-fn this #(rf/dispatch [:map/loaded]) #(rf/dispatch [:map/moved]))
       :component-will-unmount (m/destroy-fn)
       :reagent-render         (fn [] [map-view this])})))

(defn attribution []
  [:span
   "Herausgegeben von " [:a {:href "https://openfreemap.org/" :target "_blank" :rel "noopener noreferrer"} "OPENFREEMAP"]
   " " [:a {:href "https://openmaptiles.org/" :target "_blank" :rel "noopener noreferrer"} "© OPENMAPTILES"]
   " Data from " [:a {:href "https://www.openstreetmap.org/copyright" :target "_blank" :rel "noopener noreferrer"} "OPENSTREETMAP"]])

(defn scale []
  (str "Massstab 1 : " (model/format-scale @(rf/subscribe [:scale]))))

(defn map-container []
  [:div {:id "map-container"}
   [:div {:id "canvas-top"}
    [:span.left (str "Quadrat " @(rf/subscribe [:query]))]
    [:span.center [scale]]
    [:span.right "Für die Navigierung nicht zu benutzen"]]
   [canvas]
   [:div {:id "canvas-bottom"}
    [attribution]]])

(defn body []
  [:<>
   [:aside
    [:h1 "navalgrid"]
    [query-input]
    [output]]
   [:main
    [map-container]]])

(defn init []
  (rf/dispatch [:init])
  [body])