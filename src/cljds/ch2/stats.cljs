(ns cljds.ch2.stats)

(defn mean [xs]
  (/ (apply + xs)
     (count xs)))

(defn variance [xs]
  (let [m (mean xs)
        square-error (fn [x]
                       (Math/pow (- x m) 2))]
    (mean (map square-error xs))))

(defn standard-deviation [xs]
  (Math/sqrt (variance xs)))

(defn standard-error [xs]
  (/ (standard-deviation xs)
     (Math/sqrt (count xs))))

(defn sq [xs]
  (Math/pow xs 2))

(defn randn [mean sd]
  (js/jStat.normal.sample mean sd))

(defn normal-distribution [mean sd]
  (repeatedly #(randn mean sd)))

(defn randexp [lambda]
  (js/jStat.exponential.sample lambda))

(defn exponential-distribution [lambda]
  (repeatedly #(randexp lambda)))

(defn standard-error-diff [standard-error-1 standard-error-2]
  (Math/sqrt (+ (Math/pow standard-error-1 2)
                (Math/pow standard-error-2 2))))

(defn pdf-normal [x & {:keys [mean sd]
                       :or {mean 0 :sd 1}}]
  (.pdf js/jStat.normal x mean sd))

(defn pdf-t [t & {:keys [df]}]
  (.pdf js/jStat.studentt t df))

(defn t-statistic [test {:keys [mean n sd]}]
  (/ (- mean test)
     (/ sd (Math/sqrt n))))

(defn threshold-t [sides dof p]
  (let [alpha (if (= sides 2)
                (-> (- 1 p) (/ 2) (+ p))
                p)]
    (.inv js/jStat.studentt alpha dof)))
