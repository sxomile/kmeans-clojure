(ns kmeans-clojure.visual
  (:require [quil.core :as q]))

;idea is to use quil for visual representation of data

;introducing atoms
(defonce state (atom {:points []}))

(def width 500)
(def height 400)
(def point-count 10)

(defn random-color []
  [(rand-int 256) (rand-int 256) (rand-int 256)])

;for now i want points to actually be random, width and height are now variables, so the dots don't escape the window
(defn random-point []
  {:x (rand-int width)
   :y (rand-int height)
   :color (random-color)})

(defn setup []
  (reset! state
          {:points (repeatedly point-count random-point)}))

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
    (swap! state assoc
           :points (repeatedly point-count random-point))))

(defn draw []
  (swap! state update :points #(map move-point %))
  (q/background 255)
  (q/stroke 0)
  (doseq [{:keys [x y color]} (:points @state)]
    (apply q/fill color)
    (q/ellipse x y 10 10)))

(defn start []
  (q/defsketch example
               :title "K-means visual"
               :size [width height]
               :setup setup
               :draw draw
               :key-pressed key-pressed))

