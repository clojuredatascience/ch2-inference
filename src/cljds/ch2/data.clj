(ns cljds.ch2.data
  (:require [clj-time.coerce :as tc]
            [clj-time.format :as f]
            [clj-time.predicates :as p]
            [clojure.java.io :as io]
            [incanter.core :as i]
            [incanter.io :as iio]))

(defn load-data [file]
  (-> (io/resource file)
      (iio/read-dataset :header true :delim \tab)))

(defn with-parsed-date [data]
  (i/transform-col data :date (comp tc/to-local-date f/parse)))

(defn filter-weekdays [data]
  (i/$where {:date {:$fn p/weekday?}} data))

(defn mean-dwell-times-by-date [data]
  (i/$rollup :mean :dwell-time :date data))

(defn daily-mean-dwell-times [data]
  (->> (with-parsed-date data)
       (filter-weekdays)
       (mean-dwell-times-by-date)))

(defn filtered-times [filter data]
  (->> (i/$where filter data)
       (i/$ :dwell-time)))
