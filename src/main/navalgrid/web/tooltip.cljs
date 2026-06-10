(ns navalgrid.web.tooltip
  (:require [reagent.core :as r]))

(defn with-tooltip
  "Takes two arguments, text and component, decorating component with a tooltip showing text."
  []
  (let [show? (r/atom false)
        pos   (r/atom {:x 0 :y 0})]
    (fn [text component]
      [:span.tooltip {:on-mouse-enter #(reset! show? true)
                      :on-mouse-leave #(reset! show? false)
                      :on-mouse-move  (fn [e] (reset! pos {:x (+ (.-clientX e) 12)
                                                           :y (+ (.-clientY e) 12)}))
                      :tabIndex 0}
       component
       (when @show?
         [:div.tooltip {:style {:left (str (:x @pos) "px")
                                :top  (str (:y @pos) "px")
                                :z-index 1000}}
          text])])))