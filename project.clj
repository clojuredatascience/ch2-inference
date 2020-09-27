(defproject cljds/ch2 "0.1.0"
  :description "Example code for the book Clojure for Data Science"
  :url "https://github.com/clojuredatascience/ch2-inference"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.764"]
                 [incanter/incanter "1.5.7"]
                 [medley "1.3.0"]
                 [clj-time "0.15.2"]
                 [b1 "0.3.1"]
                 [reagent "1.0.0-alpha2"]]
  :resource-paths ["data"]
  :plugins [[lein-cljsbuild "1.1.8"]]
  :aot [cljds.ch2.core]
  :main cljds.ch2.core
  :repl-options {:init-ns cljds.ch2.examples}
  :profiles {:dev {:dependencies [[org.clojure/tools.cli "1.0.194"]]}}
  
  :cljsbuild
  {:builds
   {:client {:source-paths ["src"]
             :compiler
             {:output-dir "target/app"
              :output-to "target/app.js"
              :pretty-print true
              :optimizations :simple}}}})
