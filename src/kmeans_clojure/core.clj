(ns kmeans-clojure.core
  (:require [kmeans-clojure.csv :as csv-ops]
            [kmeans-clojure.kmeans :as k]
            [kmeans-clojure.visual :as v]
            [kmeans-clojure.ui :as ui]))

(defn -main []
  (ui/create-ui))

;needed to refactor since code is getting big

;next steps are csv loading and visual representation
;data will be represented visually only when it is 2d
;n-dimensions (n>2; n being whole number greater than 0) will be just represented textually, which is already kinda implemented
