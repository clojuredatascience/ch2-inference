(ns cljds.ch2.views
  (:require [cljds.ch2.models :refer [update-sample!]]))

(defn new-sample [state]
  [:a.button {:role "button" :on-click #(update-sample! state)} "New Sample"])

(defn range-controller [{:keys [range label state key-path disabled]
                         :or {disabled false}}]
  (let [value (get-in @state key-path)]
    [:div.row
     [:div.large-6.medium-4.small-7.columns
      [:label.right (str label ": " value)]]
     [:div.large-6.medium-8.small-5.columns
      [:input {:type "range"
               :value value
               :disabled disabled
               :min (apply min range)
               :max (apply max range)
               :on-change #(swap! state assoc-in key-path (-> % .-target .-value int))}]]]))
