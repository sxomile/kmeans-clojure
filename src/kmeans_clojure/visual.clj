(ns kmeans-clojure.visual
  (:require [quil.core :as q]))

;idea is to use quil for visual representation of data

;github code is now confgured just to draw some given points

(def points
  [{:x 100 :y 150}
   {:x 200 :y 80}
   {:x 300 :y 220}
   {:x 400 :y 180}])

(defn setup []
  (q/background 255))

(defn draw []
  (q/background 255) ;; clear screen every frame
  (q/fill 0)
  (q/stroke 0)
  (doseq [{:keys [x y]} points]
    (q/ellipse x y 10 10)))

(defn start []
  (q/defsketch example
               :title "K-means visual"
               :size [500 400]
               :setup setup
               :draw draw))
