(ns kmeans-clojure.visual
  (:require [quil.core :as q]
            [kmeans-clojure.kmeans :as k]))

;idea is to use quil for visual representation of data

;introducing atoms
(defonce state (atom {:points []
                      :centroids {}
                      :clusters {}  ;adding cluster atom
                      :bounds nil}))
(defonce centroid-colors (atom {}))

(def width 500)
(def height 400)
(def point-count 40)

(defn point->vec [{:keys [x y]}]
  [x y])                                                    ;algo expects vectors

(defn vec->point [[x y]]
  {:x x :y y})

(defn random-color []
  [(rand-int 256) (rand-int 256) (rand-int 256)])

(defn bounds [points]                                       ;defining bounds
  (let [xs (map first points)
        ys (map second points)]
    {:min-x (apply min xs)
     :max-x (apply max xs)
     :min-y (apply min ys)
     :max-y (apply max ys)}))

;for now i want points to actually be random, width and height are now variables, so the dots don't escape the window
(defn random-point []
  {:x (rand-int width)
   :y (rand-int height)})

(defn scale-point                                           ;need to scale points in order to display proportionally
  [{:keys [min-x max-x min-y max-y]} [x y]]
  (let [padding 40
        sx (+ padding
              (* (- x min-x)
                 (/ (- width (* 2 padding))
                    (max 1 (- max-x min-x)))))
        sy (+ padding
              (* (- y min-y)
                 (/ (- height (* 2 padding))
                    (max 1 (- max-y min-y)))))]
    {:x sx :y sy}))

(defn ensure-centroid-colors! [centroids]
  (doseq [c centroids]
    (when-not (contains? @centroid-colors c)
      (swap! centroid-colors assoc c (random-color)))))

(defn setup [raw-points k-val]
  (let [points-data (if (and raw-points (seq raw-points))
                      raw-points
                      (repeatedly point-count
                                  #(vector (rand-int 100) (rand-int 100)))) ;since csv import should be handled from ui, this need to be able to accept points and csv import should be removed from here

        ;compute scaling bounds
        b (bounds points-data)

        ;scale raw points to fit canvas
        points (vec (map (partial scale-point b) points-data))

        ;convert scaled points into vectors for kmeans
        point-vecs (mapv point->vec points)

        ;initialize centroids from scaled points
        centroids (vec (k/init-centroids point-vecs k-val))]

    ;assign stable colors by cluster index
    (reset! centroid-colors
            (into {}
                  (map (fn [i] [i (random-color)])
                       (range k-val))))

    ;initialize state
    (reset! state
            {:points points
             :centroids centroids
             :clusters {}
             :bounds b})

    (q/frame-rate 1)))

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
               :clusters  clusters
               :bounds (:bounds current-state)})))

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


  (defn start [raw-points k-val]
    (q/defsketch example
                 :title "K-means visual"
                 :size [width height]
                 :setup (fn [] (setup raw-points k-val))
                 :draw draw))               ;don't really see a need for key pressed now