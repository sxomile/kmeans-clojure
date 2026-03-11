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

;nearest centroid
(facts "nearest-centroid-basic-case" (nearest-centroid [1 1] [[0 0] [5 5]]) => [0 0])

(facts "nearest-centroid-one-centroid" (nearest-centroid [1 1] [[0 0]]) => [0 0])

(facts "nearest-centroid-3d" (nearest-centroid [1 1 1] [[0 0 0] [5 5 5]]) => [0 0 0])

(facts "nearest-centroid-centroid-identical-to-point" (nearest-centroid [1 1] [[0 0] [1 1] [5 5]]) => [1 1])

(facts "nearest-centroid-distance-tie" (nearest-centroid [0 1] [[0 0] [0 2]]) => [0 0])

;assign-clusters
(facts "assign-clusters-basic-case" (assign-clusters [[0 0] [1 1] [9 9] [10 10]] [[0 0] [10 10]])
       => {[0 0] [[0 0] [1 1]]
           [10 10] [[9 9] [10 10]]})

(facts "assign-clusters-3d" (assign-clusters [[0 0 0] [1 1 1] [9 9 9] [10 10 10]] [[0 0 0] [10 10 10]])
       => {[0 0 0] [[0 0 0] [1 1 1]]
           [10 10 10] [[9 9 9] [10 10 10]]})

(facts "assign-clusters-one-centroid" (assign-clusters [[0 0] [1 1] [10 10]] [[0 0]])
       => {[0 0] [[0 0] [1 1] [10 10]]})

(facts "assign-clusters-no-points" (assign-clusters [] [[0 0] [1 1]])
       => (throws Exception))                               ;is this really a possible use-case?

(facts "assign-clusters-no-centroids" (assign-clusters [[0 0] [1 1]] [])
       => (throws Exception))                               ;if this happens something is really messed up

;mean
(facts "mean-of-numbers" (mean [1 3 5 7 9]) => 5.0)

(facts "mean-empty" (mean []) => (throws Exception))

;mean-point
(facts "mean-point-2d" (mean-point [[1 2] [3 4]])
       => [2.0 3.0])

(facts "mean-point-3d" (mean-point [[1 2 3] [4 5 6] [7 8 9]])
      => [4.0 5.0 6.0])

(facts "mean-point-single-point" (mean-point [[5 7]])
      => [5.0 7.0])

(facts "mean-point-negative" (mean-point [[-1 -2] [1 2]])
      => [0.0 0.0])

(facts "mean-point-decimal-result" (mean-point [[1 1] [2 2]])
      => [1.5 1.5])

;recompute-centroids

(facts "recompute-centroids-simple-clusters"
       (recompute-centroids
        {[1 1] [[1 1] [3 3]]
         [10 10] [[9 9] [11 11]]})
      => [[2 2] [10 10]])

(facts "recompute-centroids-single-point-cluster"
      (recompute-centroids
        {[5 5] [[5 5]]})
      => [[5 5]])

(facts "recompute-centroids-multiple-clusters"
      (recompute-centroids
        {[1 1] [[1 1] [3 3]]
         [10 10] [[10 10] [14 14]]
         [2 2] [[2 4] [6 8]]})
      => [[2 2] [12 12] [4 6]])

(facts "recompute-centroids-empty-clusters"
      (recompute-centroids {})
      => [])