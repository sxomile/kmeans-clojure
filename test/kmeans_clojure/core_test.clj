(ns kmeans-clojure.core-test
  (:require [clojure.test :refer :all]
            [midje.sweet :refer [facts => throws roughly]]
            [kmeans-clojure.core :refer :all]))

;distance tests
(facts "distance-between-points-2d" (distance [1 2] [2 1]) =>  (roughly (Math/sqrt 2)))

(facts "distance-between-points-3d" (distance [1 6 3] [3 2 6]) => (roughly (Math/sqrt 29)))

(facts "distance-between-points-different-dimensions" (distance [1 4] [1 2 6]) => (throws Exception))

(facts "distance-between-self-returns-0" (distance [1 2] [1 2]) => 0.0)

(facts "negative-and-positive-coordinates-distance" (distance [-1 -2] [1 2]) => (roughly (Math/sqrt 20)))

(facts "distance-from-zero" (distance [0 0] [1 2]) => (roughly (Math/sqrt 5)))

(facts "distance-is-symmetrical" (= (distance [1 2] [3 4]) (distance [3 4] [1 2])) => true)

(facts "distance-empty-vectors" (distance [] []) => (throws Exception))
