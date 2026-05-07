(ns kmeans-clojure.visual
  (:require [quil.core :as q]))

;idea is to use quil for visual representation of data

;introducing atoms
(defonce state (atom {:history []
                      :step 0
                      :auto-play? true
                      :scaled-history []
                      :points []
                      :centroids {}
                      :clusters {}  ;adding cluster atom
                      :bounds nil}))
(defonce centroid-colors (atom {}))

(def width 500)
(def height 400)

(defn random-color []
  [(rand-int 256) (rand-int 256) (rand-int 256)])

(defn bounds [points]                                       ;defining bounds
  (let [xs (map first points)
        ys (map second points)]
    {:min-x (apply min xs)
     :max-x (apply max xs)
     :min-y (apply min ys)
     :max-y (apply max ys)}))

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

(defn key-pressed []
  (swap! state
         (fn [{:keys [step scaled-history] :as s}]
           (let [max-step (dec (count scaled-history))]
             (cond
               (= (q/key-as-keyword) :right)
               (update s :step #(min max-step (inc %)))

               (= (q/key-as-keyword) :left)
               (update s :step #(max 0 (dec %)))

               (= (q/key-as-keyword) :r) ;; reset
               (assoc s :step 0)

               :else s)))))

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
                   :auto-play? true
                   :bounds bounds})

    (q/frame-rate 2)))                                      ;completely rewritten o work only with history data

  (defn draw []

    ;(update-state)
    (q/background 255)


    (let [{:keys [scaled-history step auto-play?]} @state
          {:keys [centroids clusters]} (nth scaled-history step)
          last-step (dec (count scaled-history))]

      (when auto-play?
        (swap! state
               (fn [s]
                 (if (< step last-step)
                   (update s :step inc)
                   (assoc s :auto-play? false)))))

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
                 :draw draw
                 :key-pressed key-pressed))
