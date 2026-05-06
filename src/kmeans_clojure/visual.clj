(ns kmeans-clojure.visual
  (:require [quil.core :as q]
            [kmeans-clojure.kmeans :as k]))

;idea is to use quil for visual representation of data

;introducing atoms
(defonce state (atom {:history []
                      :step 0
                      :scaled-history []
                      :points []
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
    [sx sy]))

(defn scale-history [history]
  ; collect all points once to compute bounds
  (let [all-points (mapcat (fn [{:keys [clusters]}]
                             (mapcat identity (vals clusters)))
                           history)
        b (bounds all-points)]

    {:bounds b
     :scaled
     (mapv
       (fn [{:keys [centroids clusters] :as step}]
         {:centroids (mapv (partial scale-point b) centroids)
          :clusters  (into {}
                           (for [[c pts] clusters]
                             [(scale-point b c)
                              (mapv (partial scale-point b) pts)]))})
       history)}))

(defn ensure-centroid-colors! [centroids]
  (doseq [c centroids]
    (when-not (contains? @centroid-colors c)
      (swap! centroid-colors assoc c (random-color)))))

(defn setup [history]
  (let [{:keys [bounds scaled]} (scale-history history)
        k (count (:centroids (first scaled)))]

    ;; stable colors by index
    (reset! centroid-colors
            (into {}
                  (map (fn [i] [i (random-color)])
                       (range k))))

    (reset! state {:history history
                   :scaled-history scaled
                   :step 0
                   :bounds bounds})

    (q/frame-rate 1)))                                      ;completely rewritten o work only with history data

(defn update-state []
  (swap! state
         (fn [{:keys [step scaled-history] :as s}]
           (let [next-step (min (dec (count scaled-history))
                                (inc step))]
             (assoc s :step next-step)))))

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

    (update-state)
    (q/background 255)


    (let [{:keys [scaled-history step]} @state
          {:keys [centroids clusters]} (nth scaled-history step)]

      (doseq [[idx [centroid pts]]
              (map-indexed vector clusters)]
        (let [color (get @centroid-colors idx [0 0 0])]
          (apply q/fill color)
          (q/stroke 0)
          (doseq [[x y] pts]
            (q/ellipse x y 8 8))))

    (doseq [[idx [x y]]
            (map-indexed vector centroids)] ;once this is and clusters are sorted, no more random color switches occur
      (let [color (get @centroid-colors idx [0 0 0])]       ;with these indexes I am making sure that all the colors of the cluster have the same color, and not just some random one until the end
        (apply q/fill color)
        (q/stroke 0)
        (q/ellipse x y 16 16)))))


  (defn start [history]
    (q/defsketch example
                 :title "K-means visual"
                 :size [width height]
                 :setup (fn [] (setup history))
                 :draw draw))               ;don't really see a need for key pressed now