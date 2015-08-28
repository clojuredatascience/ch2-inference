(ns cljds.ch2.app
  (:require [cljds.ch2.views :refer [new-sample range-controller]]
            [cljds.ch2.models :refer [state]]
            [cljds.ch2.stats :as s]

            [reagent.core :as r]
            [b1.charts :as c]
            [b1.svg :as svg]))

(defn probability-density [sample alpha]
  (let [mu (s/mean sample)
        sd (s/standard-deviation sample)
        n  (count sample)]
    (fn [x]
      (let [df     (dec (count sample))
            t-crit (s/threshold-t 2 df alpha)
            t-stat (s/t-statistic x {:mean mu
                                     :sd sd
                                     :n n})]
        (if (< (Math/abs t-stat) t-crit)
          (s/pdf-t t-stat :df df)
          0)))))

(defn controllers [state]
  [:form
   [:fieldset
    [:legend "Parameters"]
    [range-controller {:range [5 100]
                       :label "Population A mean"
                       :state state
                       :key-path [:mean-a]}]
    [range-controller {:range [5 100]
                       :label "Population B mean"
                       :state state
                       :key-path [:mean-b]}]]
   [:fieldset
    [:legend "Settings"]
    [range-controller {:range [10 1000]
                       :label "Sample size"
                       :state state
                       :key-path [:sample-size]}]
    [range-controller {:range [50 100]
                       :label "Confidence interval"
                       :state state
                       :key-path [:alpha]}]]
   [:fieldset
    [:legend "Statistics"]
    [range-controller {:range [5 100]
                       :label "Sample A mean"
                       :state state
                       :disabled true
                       :key-path [:sample-mean-a]}]
    [range-controller {:range [5 100]
                       :label "Sample B mean"
                       :state state
                       :disabled true
                       :key-path [:sample-mean-b]}]]
   [new-sample state]])

(defn sample-histograms [sample-a sample-b]
  (-> (c/histogram sample-a :x-axis [0 200] :bins 20)
      (c/add-histogram sample-b)
      (svg/as-svg :width 550 :height 400)))

(defn sample-means [sample-a sample-b alpha]
  (-> (c/function-area-plot (probability-density sample-a alpha)
                            :x-axis [0 200])
      (c/add-function (probability-density sample-b alpha))
      (svg/as-svg :width 550 :height 250)))

(defn layout-interface []
  (let [sample-a (get @state :sample-a)
        sample-b (get @state :sample-b)
        alpha (/ (get @state :alpha) 100)]
    [:div
     [:div.row
      [:div.large-12.columns
       [:h1 "Parameters & Statistics"]]]
     [:div.row
      [:div.large-5.large-push-7.columns
       [controllers state]]
      [:div.large-7.large-pull-5.columns {:role :content}
       [sample-histograms sample-a sample-b]
       [sample-means sample-a sample-b alpha]]]]))

(defn run []
  (r/render-component
   [layout-interface]
   (.getElementById js/document "root")))
