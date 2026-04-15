(ns kmeans-clojure.visual
  (:require [quil.core :as q]
            [kmeans-clojure.kmeans :as k]))

;idea is to use quil for visual representation of data

;introducing atoms
(defonce state (atom {:points [] :centroids []}))
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

(defn setup []
  (let [centroids (vec (repeatedly centroid-count random-centroid))
        points (vec
                 (map (fn [p]
                        (assoc p :centroid (k/nearest-centroid (point->vec p) centroids)))
                      (repeatedly point-count random-point)))]
    (reset! centroid-colors
            (into {}
                  (map (fn [c] [c (random-color)]) centroids)))
    (reset! state
            {:points points
             :centroids centroids})))

;this helper function is for moving points around the screen
;introducing moving points, which will be important in some of the future steps (animation of algo)
(defn move-point [{:keys [x y] :as point}]
  (assoc point                                              ;this part was making issues with coloring, returned some nils so it threw exceptions
    :x (-> x (+ (- (rand-int 5) 2)) (max 0) (min width))    ;to move +- 2 px
    :y (-> y (+ (- (rand-int 5) 2)) (max 0) (min height))))

;using r for example to change the state of points, i may change the key later
;looks pretty good honestly
(defn key-pressed []
  (when (= (q/key-as-keyword) :r)
    (setup)))

(defn draw []
  (swap! state update :points #(map move-point %))
  (q/background 255)
  (q/stroke 0)
  (doseq [{:keys [x y centroid]} (:points @state)]
    (let [color (get @centroid-colors centroid [0 0 0])]
      (apply q/fill color)
      (q/ellipse x y 8 8)))
  (doseq [[x y :as c] (:centroids @state)]                  ;change of format to be compatible with algo
    (let [color (get @centroid-colors c [0 0 0])]
      (apply q/fill color)
      (q/stroke 0)
      (q/ellipse x y 16 16))))
;ok finally the "centroids" are visible
;other dots just pick one color from each centroid
;this is the end of mocking and playground stage, next step is to try to actually group dots
;then get closer and closer to actual integration of algorithm into visuals

;now dots get the color of the closest centroid, although it doesn't change while they move

(defn start []
  (q/defsketch example
               :title "K-means visual"
               :size [width height]
               :setup setup
               :draw draw
               :key-pressed key-pressed))







