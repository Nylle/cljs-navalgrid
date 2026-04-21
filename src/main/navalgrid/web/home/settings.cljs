(ns navalgrid.web.home.settings
  (:require [re-frame.core :as rf]
            [navalgrid.domain.geo :as geo]
            [navalgrid.web.storage :as s]))

(rf/reg-sub :prefs/format (fn [db _] (:format db)))

(rf/reg-event-fx
  :prefs/set-format
  (fn [{:keys [db]} [_ fmt]]
    {:db     (assoc db :format fmt)
     :run-do (fn [] (s/ls-set! :format fmt))}))

(def formats
  (let [example [52.52001 13.40495]]
    [[:dms (str (geo/coords->str example :dms) " (DMS)")]
     [:dmm (str (geo/coords->str example :dmm) " (DMM)")]
     [:dd (str (geo/coords->str example :dd) " (DD)")]
     [:jerry (str (geo/coords->str example :jerry) " (Concise)")]
     [:deg (str (geo/coords->str example :deg) " (Signed Decimal)")]]))

(defn format-selector []
  (let [current (rf/subscribe [:prefs/format])]
    (fn []
      [:fieldset.format
       {:aria-label "Coordinate display format"}
       (into [:<>] (for [[k label] formats]
         ^{:key (name k)}
         [:label.format__option
          [:input {:type      "radio"
                   :name      "format"
                   :value     (name k)
                   :checked   (= @current k)
                   :on-change (fn [_] (rf/dispatch [:prefs/set-format k]))}]
          [:span.label-text label]]))])))