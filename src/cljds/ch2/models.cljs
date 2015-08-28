(ns cljds.ch2.models
  (:require [reagent.core :as r]
            [cljds.ch2.stats :refer [exponential-distribution mean]]))

(defn update-sample [{:keys [mean-a mean-b sample-size]
                      :as state}]
  (let [sample-a (->> (float (/ 1 mean-a))
                      (exponential-distribution)
                      (take sample-size))
        sample-b (->> (float (/ 1 mean-b))
                      (exponential-distribution)
                      (take sample-size))]
    (-> state
        (assoc :sample-a sample-a)
        (assoc :sample-b sample-b)
        (assoc :sample-mean-a (int (mean sample-a)))
        (assoc :sample-mean-b (int (mean sample-b))))))

(defn update-sample! [state]
  (swap! state update-sample))

(def state
  (doto (r/atom {:sample-a []
                 :sample-b []
                 :sample-mean-a nil
                 :sample-mean-b nil
                 :mean-a 60
                 :mean-b 60
                 :sample-size 50
                 :alpha 95})
    (update-sample!)))
