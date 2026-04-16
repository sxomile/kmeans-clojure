(ns kmeans-clojure.visual
  (:require [quil.core :as q]
            [kmeans-clojure.kmeans :as k]))

;idea is to use quil for visual representation of data

;introducing atoms
(defonce state (atom {:points [] :centroids {}}))
(defonce centroid-colors (atom {}))

(def width 500)
(def height 400)
(def point-count 40)
(def centroid-count 3)

(defn point->vec [{:keys [x y]}]
  [x y])                                                    ;algo expects vectors

(defn random-color []
  [(rand-int 256) (rand-int 256) (rand-int 256)])

(defn random-centroid []
  [(rand-int width) (rand-int height)])                     ;to match expected format

;for now i want points to actually be random, width and height are now variables, so the dots don't escape the window
(defn random-point []
  {:x (rand-int width)
   :y (rand-int height)})

(defn ensure-centroid-colors! [centroids]
  (doseq [c centroids]
    (when-not (contains? @centroid-colors c)
      (swap! centroid-colors assoc c (random-color)))))

(defn setup []
  (let [centroids (vec (repeatedly centroid-count random-centroid))
        points (vec (repeatedly point-count random-point))]
    (reset! centroid-colors
            (into {}
                  (map (fn [i] [i (random-color)])
                       (range centroid-count))))
    (reset! state
            {:points    points
             :centroids centroids
             :clusters  {}})
    (q/frame-rate 1)))

  ;using r for example to change the state of points, i may change the key later
  ;looks pretty good honestly
  (defn key-pressed []
    (when (= (q/key-as-keyword) :r)
      (setup)))

  (defn update-kmeans []
    (let [current-state @state
          fixed-points (:points current-state)
          point-vectors (mapv point->vec fixed-points)
          result (k/kmeans-step point-vectors
                                (:centroids current-state))
          new-centroids (:centroids result)
          clusters (:clusters result)]
      (ensure-centroid-colors! new-centroids)
      (reset! state
              {:points    fixed-points
               :centroids new-centroids
               :clusters  clusters})))

  (defn draw []

    (update-kmeans)
    (q/background 255)

    (doseq [[idx [centroid pts]]
            (map-indexed vector
                         (sort-by (fn [[centroid _]] centroid)
                                  (:clusters @state)))]
      (let [color (get @centroid-colors idx [0 0 0])]
        (apply q/fill color)
        (q/stroke 0)
        (doseq [[x y] pts]
          (q/ellipse x y 8 8))))

    (doseq [[idx [x y]]
            (map-indexed vector
                         (sort-by identity (:centroids @state)))] ;once this is and clusters are sorted, no more random color switches occur
      (let [color (get @centroid-colors idx [0 0 0])]       ;with these indexes I am making sure that all the colors of the cluster have the same color, and not just some random one until the end
        (apply q/fill color)
        (q/stroke 0)
        (q/ellipse x y 16 16))))

;now dots get the color of the closest centroid, although it doesn't change while they move
;i mean it works but sometimes 2 clusters randomly switch colors, probably because clusters are unordered as of right now

  (defn start []
    (q/defsketch example
                 :title "K-means visual"
                 :size [width height]
                 :setup setup
                 :draw draw
                 :key-pressed key-pressed))
