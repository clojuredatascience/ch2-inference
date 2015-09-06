(ns cljds.ch2.examples
  (:require [cljds.ch2.data :refer :all]
            [cljds.ch2.stats :refer :all]
            [clj-time.format :as f]
            [clj-time.predicates :as p]
            [medley.core :refer [map-vals]]
            [clj-time.core :as t]
            [incanter.charts :as c]
            [incanter.core :as i]
            [incanter.io :as iio]
            [incanter.stats :as s]
            [incanter.svg :as svg]))

(defn ex-2-1 []
  (-> (load-data "dwell-times.tsv")
      (i/view)))

(defn ex-2-2 []
  (-> (i/$ :dwell-time (load-data "dwell-times.tsv"))
      (c/histogram :x-label "Dwell time (s)"
                   :nbins 50)
      (i/view)))

(defn ex-2-3 []
  (-> (i/$ :dwell-time (load-data "dwell-times.tsv"))
      (c/histogram :x-label "Dwell time (s)"
                   :nbins 20)
      (c/set-axis :y (c/log-axis :label "Log Frequency"))
      (i/view)))

(defn ex-2-4 []
  (let [dwell-times (->> (load-data "dwell-times.tsv")
                         (i/$ :dwell-time))]
    (println "Mean:  " (s/mean dwell-times))
    (println "Median:" (s/median dwell-times))
    (println "SD:    " (s/sd dwell-times))))

(defn ex-2-5 []
  (let [means (->> (load-data "dwell-times.tsv")
                   (daily-mean-dwell-times)
                   (i/$ :dwell-time))]
    (println "Mean:   " (s/mean means))
    (println "Median: " (s/median means))
    (println "SD:     " (s/sd means))))

(defn ex-2-6 []
  (let [means (->> (load-data "dwell-times.tsv")
                   (daily-mean-dwell-times)
                   (i/$ :dwell-time))]
    (-> (c/histogram means
                     :x-label "Daily mean dwell time (s)"
                     :nbins 20)
        (i/view))))

(defn ex-2-7 []
  (let [means (->> (load-data "dwell-times.tsv")
                   (daily-mean-dwell-times)
                   (i/$ :dwell-time))
        mean (s/mean means)
        sd   (s/sd means)
        pdf  (fn [x]
               (s/pdf-normal x :mean mean :sd sd))]
    (-> (c/histogram means
                     :x-label "Daily mean dwell time (s)"
                     :nbins 20
                     :density true)
        (c/add-function pdf 80 100)
        (i/view))))

;; May 1st

(defn ex-2-8 []
  (let [may-1 (f/parse-local-date "2015-05-01")]
    (->> (load-data "dwell-times.tsv")
         (with-parsed-date)
         (filtered-times {:date {:$eq may-1}})
         (standard-error))))

(defn ex-2-9 []
  (let [may-1 (f/parse-local-date "2015-05-01")]
    (->> (load-data "dwell-times.tsv")
         (with-parsed-date)
         (filtered-times {:date {:$eq may-1}})
         (confidence-interval 0.95))))

;; Sample times

(defn ex-2-10 []
  (let [times (->> (load-data "campaign-sample.tsv")
                   (i/$ :dwell-time))]
    (println "n:      " (count times))
    (println "Mean:   " (s/mean times))
    (println "Median: " (s/median times))
    (println "SD:     " (s/sd times))
    (println "SE:     " (standard-error times))))

(defn ex-2-11 []
  (->> (load-data "campaign-sample.tsv")
       (i/$ :dwell-time)
       (confidence-interval 0.95)))

;; Overall dwell times

(defn ex-2-12 []
  (let [means (->> (load-data "dwell-times.tsv")
                   (with-parsed-date)
                   (mean-dwell-times-by-date)
                   (i/$ :dwell-time))]
    (-> (c/histogram means
                     :x-label "Daily mean dwell time unfiltered (s)"
                     :nbins 20)
        (i/view))))

(defn ex-2-13 []
  (let [weekend-times (->> (load-data "dwell-times.tsv")
                           (with-parsed-date)
                           (i/$where {:date {:$fn p/weekend?}})
                           (i/$ :dwell-time))]
    (println "n:      " (count weekend-times))
    (println "Mean:   " (s/mean weekend-times))
    (println "Median: " (s/median weekend-times))
    (println "SD:     " (s/sd weekend-times))
    (println "SE:     " (standard-error weekend-times))))

;; n:       5860
;; Mean:    117.78686006825939
;; Median:  81.0
;; SD:      120.65234077179436
;; SE:      1.5759770362547665


(defn ex-2-14 []
  (let [data (->> (load-data "new-site.tsv")
                  (:rows)
                  (group-by :site)
                  (map-vals (partial map :dwell-time)))
        a (get data 0)
        b (get data 1)]
    (println "a n:" (count a))
    (println "b n:" (count b))
    (println "z-stat: " (z-stat a b))
    (println "p-value:" (z-test a b))))

(defn ex-2-15 []
  (let [data (->> (load-data "new-site.tsv")
                  (:rows)
                  (group-by :site)
                  (map-vals (partial map :dwell-time)))
        a (get data 0)
        b (get data 1)]
    (t-stat a b)))

(defn ex-2-16 []
  (let [data (->> (load-data "new-site.tsv")
                  (:rows)
                  (group-by :site)
                  (map-vals (partial map :dwell-time)))
        a (get data 0)
        b (get data 1)]
    (t-test a b)))

(defn ex-2-17 []
  (let [data (->> (load-data "new-site.tsv")
                  (:rows)
                  (group-by :site)
                  (map-vals (partial map :dwell-time)))
        a (get data 0)
        b (get data 1)]
    (clojure.pprint/pprint (s/t-test a :y b))))

;; {:p-value 0.12756432502462456,
;;  :df 17.7613823496861,
;;  :n2 16,
;;  :x-mean 87.95070422535211,
;;  :y-mean 122.0,
;;  :x-var 10463.941024237305,
;;  :conf-int [-78.9894629402365 10.890871390940724],
;;  :y-var 6669.866666666667,
;;  :t-stat -1.5985205593851322,
;;  :n1 284}

(defn ex-2-18 []
  (let [data (->> (load-data "new-site.tsv")
                  (:rows)
                  (group-by :site)
                  (map-vals (partial map :dwell-time)))
        b (get data 1)]
    (clojure.pprint/pprint (s/t-test b :mu 90))))

;; {:p-value 0.13789520958229406,
;;  :df 15,
;;  :n2 nil,
;;  :x-mean 122.0,
;;  :y-mean nil,
;;  :x-var 6669.866666666667,
;;  :conf-int [78.48152745280898 165.51847254719104],
;;  :y-var nil,
;;  :t-stat 1.5672973291495713,
;;  :n1 16}

(defn ex-2-19 []
  (let [data (->> (load-data "new-site.tsv")
                  (i/$where {:site {:$eq 1}})
                  (i/$ :dwell-time ))]
    (-> (s/bootstrap data s/mean :size 10000)
        (c/histogram :nbins 20
                     :x-label "Bootstrapped mean dwell times (s)")
        (i/view))))

(defn ex-2-20 []
  (->> (i/transform-col (load-data "multiple-sites.tsv")
                        :dwell-time float)
       (i/$rollup :mean :dwell-time :site)
       (i/$order :dwell-time :desc)
       (i/view)))

(defn ex-2-21 []
  (let [data (->> (load-data "multiple-sites.tsv")
                  (:rows)
                  (group-by :site)
                  (map-vals (partial map :dwell-time)))
        alpha 0.05]
    (doseq [[site-a times-a] data
            [site-b times-b] data
            :when (> site-a site-b)
            :let [p-val (-> (s/t-test times-a :y times-b)
                            (:p-value))]]
      (when (< p-val alpha)
        (println site-b "and" site-a
                 "are significantly different:"
                 (format "%.3f" p-val))))))

(defn ex-2-22 []
  (let [data (->> (load-data "multiple-sites.tsv")
                  (:rows)
                  (group-by :site)
                  (map-vals (partial map :dwell-time)))
        baseline (get data 0)
        alpha 0.05]
    (doseq [[site-a times-a] data
            :let [p-val (-> (s/t-test times-a :y baseline)
                            (:p-value))]]
      (when (< p-val alpha)
        (println site-a
                 "is significantly different from baseline:"
                 (format "%.3f" p-val))))))

(defn ex-2-23 []
  (let [data (->> (load-data "multiple-sites.tsv")
                  (:rows)
                  (group-by :site)
                  (map-vals (partial map :dwell-time)))
        alpha (/ 0.05 (count data))]
    (doseq [[site-a times-a] data
            [site-b times-b] data
            :when (> site-a site-b)
            :let [p-val (-> (s/t-test times-a :y times-b)
                            (:p-value))]]
      (when (< p-val alpha)
        (println site-b "and" site-a
                 "are significantly different:"
                 (format "%.3f" p-val))))))

(defn ex-2-24 []
  (let [grouped (->> (load-data "multiple-sites.tsv")
                     (:rows)
                     (group-by :site)
                     (vals)
                     (map (partial map :dwell-time)))]
    (f-test grouped)))

(defn ex-2-25 []
  (let [grouped (->> (load-data "multiple-sites.tsv")
                     (:rows)
                     (group-by :site)
                     (sort-by first)
                     (map second)
                     (map (partial map :dwell-time)))
        box-plot (c/box-plot (first grouped)
                             :x-label "Site number"
                             :y-label "Dwell time (s)")
        add-box (fn [chart dwell-times]
                  (c/add-box-plot chart dwell-times))]
    (-> (reduce add-box box-plot (rest grouped))
        (i/view))))

(defn ex-2-26 []
  (let [data (load-data "multiple-sites.tsv")
        site-0 (->> (i/$where {:site {:$eq 0}} data)
                    (i/$ :dwell-time))
        site-10 (->> (i/$where {:site {:$eq 10}} data)
                     (i/$ :dwell-time))]
    (s/t-test site-10 :y site-0)))

(defn ex-2-27 []
  (let [data (load-data "multiple-sites.tsv")
        site-0 (->> (i/$where {:site {:$eq 0}} data)
                    (i/$ :dwell-time))
        site-6 (->> (i/$where {:site {:$eq 6}} data)
                    (i/$ :dwell-time))]
    (s/t-test site-6 :y site-0)))

(defn ex-2-28 []
  (let [data (load-data "multiple-sites.tsv")
        a (->> (i/$where {:site {:$eq 0}} data)
               (i/$ :dwell-time))
        b (->> (i/$where {:site {:$eq 6}} data)
               (i/$ :dwell-time))]
    (/ (- (s/mean b)
          (s/mean a))
       (pooled-standard-deviation a b))))
