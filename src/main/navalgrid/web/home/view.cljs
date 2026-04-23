(ns navalgrid.web.home.view
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [navalgrid.domain.geo :as geo]
            [navalgrid.web.clipboard :as c]
            [navalgrid.web.modal :refer [modal]]
            [navalgrid.map.core :as m]
            [navalgrid.web.home.model :as model]
            [navalgrid.web.home.events]
            [navalgrid.web.home.settings :as settings]))

(rf/reg-sub :query (fn [db _] (:query db)))
(rf/reg-sub :square (fn [db _] (:square db)))
(rf/reg-sub :scale (fn [db _] (:scale db)))
(rf/reg-sub :region (fn [db _] (:region db)))
(rf/reg-sub :format (fn [db _] (:format db)))


(defn coord [x]
  (let [format @(rf/subscribe [:format])
        value (geo/coords->str x format)]
    [:span.coord {:title "Copy to Clipboard" :on-click #(c/copy! value)} value]))

(defn insert-a-umlaut [el]
  (fn [] (rf/dispatch [:query/changed "Ä"]) (when-let [n @el] (.focus n))))

(defn query-input []
  (let [el (r/atom nil)]
    (fn []
      [:span.query
       [:button.left {:title    "Insert Ä"
                      :on-click (insert-a-umlaut el)} "Ä"]
       [:input {:type        "text"
                :placeholder "Square Reference…"
                :ref         (fn [n] (reset! el n))
                :value       @(rf/subscribe [:query])
                :on-change   #(rf/dispatch [:query/changed (-> % .-target .-value)])}]
       [:button.right {:type     "button"
                       :title    "Settings"
                       :on-click (fn [] (rf/dispatch [:modal/open {:title "Coordinates Format" :body [settings/format-selector]}]))} [:i "settings"]]])))

(defn details [square]
  (let [res [:dl
             [:dt "Centre"] [:dd [coord (:center square)]]
             [:dt.gap "Label"] [:dd.gap [coord (:label square)]]
             [:dt "Height"] [:dd (str (get-in square [:dimensions :height]) " nmi")]
             [:dt "Mean Width"] [:dd (str (get-in square [:dimensions :mean-width]) " nmi")]
             [:dt "Max Width"] [:dd (str (get-in square [:dimensions :max-width]) " nmi")]
             [:dt.gap "Min Width"] [:dd.gap (str (get-in square [:dimensions :min-width]) " nmi")]]]
    (if (:poly square)
      (let [letters (cons "NW" (map #(str (char %) ")") (range 98 123)))]
        (into res (mapcat (fn [a b] [[:dt a] [:dd [coord b]]]) letters (:poly square))))
      (into res
            [[:dt "NW"] [:dd [coord (:nw square)]]
             [:dt "NE"] [:dd [coord [(first (:nw square)) (second (:se square))]]]
             [:dt "SE"] [:dd [coord (:se square)]]
             [:dt "SW"] [:dd [coord [(first (:se square)) (second (:nw square))]]]]))))

(defn output []
  (if-let [square @(rf/subscribe [:square])]
    [:<>
     [:div.region (:name @(rf/subscribe [:region]))]
     [details square]]
    [:div ""]))

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

(defn nav []
  [:div#nav
   [:img {:src "/images/logo.png"}]
   [:span "Naval Grid"]])                                   ;[:br] [:i "info"] [:i "help"] [:i "favorite"]

(defn body []
  [:<>
   [:aside
    [nav]
    [query-input]
    [output]]
   [:main
    [:div {:id "map-container"}
     [:div {:id "canvas-top"}
      [:span.left [region]]
      [:span.center [scale]]
      [:span.right "Für die Navigierung nicht zu benutzen"]]
     [canvas]
     [:div {:id "canvas-bottom"}
      [attribution]]]]])

(defn init []
  [:<>
   [modal]
   [body]])