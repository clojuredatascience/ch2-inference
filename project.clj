(defproject cljds/ch2 "0.1.0"
  :description "Example code for the book Clojure for Data Science"
  :url "https://github.com/clojuredatascience/ch2-inference"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0-RC1"]
                 [org.clojure/clojurescript "0.0-3308"]
                 [incanter/incanter "1.5.6"]
                 [medley "0.6.0"]
                 [clj-time "0.9.0"]
                 [b1 "0.3.1"]
                 [reagent "0.4.3"]]
  :resource-paths ["data"]
  :plugins [[lein-cljsbuild "1.0.5"]]
  :aot [cljds.ch2.core]
  :main cljds.ch2.core
  :repl-options {:init-ns cljds.ch2.examples}
  :profiles {:dev {:dependencies [[org.clojure/tools.cli "0.3.1"]]}}
  
  :cljsbuild
  {:builds
   {:client {:source-paths ["src"]
             :compiler
             {:preamble ["reagent/react.js"]
              :output-dir "target/app"
              :output-to "target/app.js"
              :pretty-print true}}}})
