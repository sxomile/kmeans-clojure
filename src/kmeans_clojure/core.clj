(ns kmeans-clojure.core)

;input parameters will be represented as vector of many different n-dimensional vectors
;idea is to divide these parameters in k dimensions, using k-means algo
;basic concepts are points, and dataset which is vector of points

;exmaple of simple dataset below
(def data
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

(defn distance [p1 p2]
  (when (not= (count p1) (count p2)) (throw (Exception. "Points must be of the same dimension!")))
  (when (some empty? [p1 p2]) (throw (Exception. "Points cannot be empty!")))
  (Math/sqrt (reduce + (map (fn [a b] (Math/pow (- a b) 2)) p1 p2))))

;another important piece of logic for k-means is determining the nearest centroid
;we need a function that will determine which centroid is the nearest to point,
;in order to determine which cluster it will be in
;distance can help with this part
;parameters will be the point and centroids;
(defn nearest-centroid [point centroids]
  (let [distances (map #(distance point %) centroids)
        min-index (.indexOf distances (apply min distances))]
    (nth centroids min-index)))

;in order not to iterate through every point every time, it would make life easier to use nearest-centroid fun to
;help us build function that will assign vector of points to different clusters

(defn assign-clusters [] nil)

