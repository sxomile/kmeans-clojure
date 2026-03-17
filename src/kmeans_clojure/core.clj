(ns kmeans-clojure.core)

;input parameters will be represented as vector of many different n-dimensional vectors
;idea is to divide these parameters in k dimensions, using k-means algo
;basic concepts are points, and dataset which is vector of points

;exmaple of simple dataset below
(def data
  [[1 1] [2 2] [1.5 1.2] [2.1 1.8] [0.8 1.5]
   [8 8] [9 9] [8.5 8.2] [9.1 8.7] [7.8 8.3]
   [5 1] [5.5 1.2] [4.8 0.9] [6 1.5] [5.2 1.8]
   [3 7] [3.5 7.2] [2.8 6.9] [3.2 7.5] [3.1 6.8]
   [7 3] [7.2 2.8] [6.8 3.1] [7.1 3.3] [6.9 2.9]
   [0 8] [0.5 8.2] [0.8 7.5] [0.3 7.8] [0.9 8.1]
   [9 0] [8.5 0.3] [9.2 0.5] [9.1 0.8] [8.8 0.1]
   [4 4] [4.2 3.8] [3.8 4.1] [4.1 4.2] [3.9 3.9]])          ;used ai to generate all these dots, so i can play with it in repl

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

(defn assign-clusters [points centroids]
  (when (or (empty? points) (empty? centroids)) (throw (Exception. "")))
  (group-by #(nearest-centroid % centroids) points))

;we also need a mean function, which is essential for recalculation of centroids
(defn mean [values]
  (when (= (count values) 0)
    (throw (Exception. "Empty values passed to mean function!")))
  (/ (double (reduce + values)) (count values)))

;based on simple mean function, we can now use that fun to calculate mean of n-dimensional points
(defn mean-point [points]
  (let [dim (count (first points))]
    (vec (for [i (range dim)]
           (mean (map #(nth % i) points))))))

;centroids need to be recomputed after the initial initialization
(defn recompute-centroids [clusters] (mapv (fn [[_ points]] (mean-point points)) clusters))

;when centroids stop converging, algorithm is done. so we need a function to determine if it finished converging
(defn converged? [old-centroids new-centroids]
  (= old-centroids new-centroids))

;there needs to be a function to initialize centroids in order to start with the algorithm
(defn init-centroids [points k] (take k (shuffle points)))

;now there are enough functions to define the step of k-means algorithm
;one step means goes like: assign clusters to the points; recalculate centroids
;result will be a map
(defn kmeans-step [points centroids]
  (let [clusters (assign-clusters points centroids)
        new-centroids (recompute-centroids clusters)]
    {:clusters clusters
     :centroids new-centroids}))

;now we have all the functions we need for full implementation of the algorithm4
;kmeans function is basically just supposet to "orchestrate" previously written functions
(defn kmeans
  "K-means debug output.
   points       - dataset (vector of vectors)
   k            - number of clusters
   max-iterations - optional max iterations (default 1000)"
  ([points k]
   (kmeans points k 1000))
  ([points k max-iterations]
   (let [initial-centroids (init-centroids points k)]
     (println "Initial centroids:" initial-centroids)
     (loop [centroids initial-centroids
            i 0]
       (let [result (kmeans-step points centroids)
             clusters (:clusters result)
             new-centroids (:centroids result)]

         (println "\nIteration" (inc i))
         (println "Clusters assigned:")
         (doseq [[centroid pts] clusters]
           (println "Centroid:" centroid "-> Points:" pts))
         (println "New centroids:" new-centroids)

         (if (or (converged? centroids new-centroids)
                 (>= i max-iterations))
           (do
             (println "\nConverged after" (inc i) "iterations!")
             {:clusters clusters
              :centroids new-centroids})
           (recur new-centroids (inc i))))))))

