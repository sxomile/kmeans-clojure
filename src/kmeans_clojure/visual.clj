(ns kmeans-clojure.visual
  (:require [quil.core :as q]))

;idea is to use quil for visual representation of data

;introducing atoms
(defonce state (atom {:points []}))

(def width 500)
(def height 400)
(def point-count 10)

;for now i want points to actually be random, width and height are now variables, so the dots don't escape the window
(defn random-point []
  {:x (rand-int width)
   :y (rand-int height)})

(defn setup []
  (reset! state
          {:points (repeatedly point-count random-point)}))

;this helper function is for moving points around the screen
;introducing moving points, which will be important in some of the future steps (animation of algo)
(defn move-point [{:keys [x y]}]
  {:x (-> x (+ (- (rand-int 5) 2)) (max 0) (min width))     ;to move +- 2 px
   :y (-> y (+ (- (rand-int 5) 2)) (max 0) (min height))})

(defn draw []
  (swap! state update :points #(map move-point %))
  (q/background 255)
  (q/fill 0)
  (q/stroke 0)
  (doseq [{:keys [x y]} (:points @state)]
    (q/ellipse x y 10 10)))

(defn start []
  (q/defsketch example
               :title "K-means visual"
               :size [width height]
               :setup setup
               :draw draw))

