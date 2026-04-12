(ns navalgrid.web.home.view
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [navalgrid.web.clipboard :as c]
            [navalgrid.map.core :as m]
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
(rf/reg-sub :region (fn [db _] (:region db)))

(defn coord [x]
  (let [value (str (first x) ", " (second x))]
    [:span.coord {:title "Copy to Clipboard" :on-click #(c/copy! value)} value]))

(defn query-input []
  (let [el (r/atom nil)]
    (fn []
      [:span.query
       [:button.left {:title    "Insert Ä"
                      :on-click (fn [] (rf/dispatch [:query/changed "Ä"]) (when-let [n @el] (.focus n)))} "Ä"]
       [:input {:type        "text"
                :placeholder "Square Reference…"
                :ref         (fn [n] (reset! el n))
                :value       @(rf/subscribe [:query])
                :on-change   #(rf/dispatch [:query/changed (-> % .-target .-value)])}]
       [:button.right {:type     "button"
                       :title    "Reverse Search Coming Soon…"
                       :disabled true
                       :style    {:opacity    0.5
                                  :background "#eee"
                                  :color      "#888"}} [:i "search"]]])))

(defn regular [square]
  [:dl
   [:dt.gap "Centre"] [:dd.gap [coord (:center square)]]
   [:dt "NW"] [:dd [coord (:nw square)]]
   [:dt "NE"] [:dd [coord [(first (:nw square)) (second (:se square))]]]
   [:dt "SE"] [:dd [coord (:se square)]]
   [:dt "SW"] [:dd [coord [(first (:se square)) (second (:nw square))]]]])

(defn poly [square]
  (let [letters (cons "NW" (map #(str (char %) ")") (range 98 123)))]
    (into [:dl [:dt.gap "Centre"] [:dd.gap [coord (:center square)]]]
          (mapcat (fn [a b] [[:dt a] [:dd [coord b]]]) letters (:poly square)))))

(defn square-details [res]
  [:<>
   [:div.region (:name @(rf/subscribe [:region]))]
   (if (:poly res)
     [poly res]
     [regular res])])

(defn output []
  (let [res @(rf/subscribe [:square])]
    (if res
      [square-details res]
      [:div ""])))

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

(defn region []
  [:span
   [:img {:src "/images/icon.png" :width "20" :style {:vertical-align "bottom"}}]
   (str " " (:label @(rf/subscribe [:region])))])

(defn scale []
  (str "Massstab 1 : " (model/format-scale @(rf/subscribe [:scale]))))

(defn map-container []
  [:div {:id "map-container"}
   [:div {:id "canvas-top"}
    [:span.left [region]]
    [:span.center [scale]]
    [:span.right "Für die Navigierung nicht zu benutzen"]]
   [canvas]
   [:div {:id "canvas-bottom"}
    [attribution]]])

(defn nav []
  [:div#nav
   [:img {:src "/images/logo.png"}]
   [:span "Naval Grid" [:br] [:i "settings"] [:i "info"] [:i "help"] [:i "favorite"]]])

(defn body []
  [:<>
   [:aside
    [nav]
    [query-input]
    [output]]
   [:main
    [map-container]]])

(defn init []
  (rf/dispatch [:init])
  [body])