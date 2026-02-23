(defproject kmeans-clojure "0.1.0-SNAPSHOT"
  :description "kmeans algo implementation"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [midje "1.10.9"]]
  :plugins [[lein-midje "3.2.1"]]
  :main ^:skip-aot kmeans-clojure.core)
