(ns kmeans-clojure.core)

;input parameters will be represented as vector of many different n-dimensional vectors
;idea is to divide these parameters in k dimensions, using k-means algo
;basic concepts are points, and dataset which is vector of points

;exmaple of simple dataset below
(defn data
  [[1 2]
   [3 1]
   [5 7]
   [10 4]])

;another important concept are centroids, the points which represent "typical" member of each cluster
;centroid will be introduced later

;in the beginning, points will mostly be 2d, since they are the easiest to visualize, but that might expand to more dimensions
;functions will be written in a way that works in n dimensions, but the representation of data decision is still not clear

;development will be test-driven for the most part

;the first important function is for calculation of distance between points, but some tests will be written first
;entire algorithm is based on distance calculation, so it is one of the most important functions


