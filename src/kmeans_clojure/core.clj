(ns kmeans-clojure.core
  (:require [kmeans-clojure.csv :as csv-ops])
  (:require [kmeans-clojure.kmeans :as k]))

(defn -main []
  (println "woohoo")                                        ;project couldn't run without main
  (csv-ops/load-points-via-dialog))                         ;manually tested, since i can't even run it from repl

;needed to refactor since code is getting big

;next steps are csv loading and visual representation
;data will be represented visually only when it is 2d
;n-dimensions (n>2; n being whole number greater than 0) will be just represented textually, which is already kinda implemented
