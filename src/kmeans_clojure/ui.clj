(ns kmeans-clojure.ui
  (:require [kmeans-clojure.csv :as csv]
            [kmeans-clojure.visual :as visual])
  (:import [javax.swing JFrame JButton JLabel JPanel BoxLayout Box BorderFactory]
           [java.awt Dimension Color Font]))

(defonce app-state (atom {:points nil}))

(defn is-2d? [points]
  (and (seq points)
       (= 2 (count (first points)))))                       ;I will show visualize button only if data is 2d

(defn dataset-info [points]
  (if (nil? points)
    "No dataset loaded"
    (str "Points: " (count points)
         " | Dimensions: " (count (first points)))))

(defn create-ui []
  (let [frame (JFrame. "K-Means App")
        root (JPanel.)                                      ; outer panel (background)

        panel (JPanel.)                                     ;inner panel (card)

        load-btn (JButton. "Load CSV")
        visualize-btn (JButton. "Visualize")
        info-label (JLabel. "No dataset loaded")]

    ; ===== ROOT PANEL (background) =====
    (.setBackground root (Color. 240 242 245))
    (.setLayout root (BoxLayout. root BoxLayout/Y_AXIS))

    ; ===== INNER PANEL (card style) =====
    (.setLayout panel (BoxLayout. panel BoxLayout/Y_AXIS))
    (.setBackground panel Color/WHITE)
    (.setBorder panel (BorderFactory/createEmptyBorder 20 30 20 30))

    ; ===== FONTS =====
    (.setFont info-label (Font. "Arial" Font/PLAIN 14))

    ; ===== BUTTON STYLE =====
    (doseq [btn [load-btn visualize-btn]]
      (.setFocusPainted btn false)
      (.setFont btn (Font. "Arial" Font/BOLD 14))
      (.setBackground btn (Color. 66 133 244)) ; blue
      (.setForeground btn Color/WHITE)
      (.setMaximumSize btn (Dimension. 200 40))
      (.setAlignmentX btn 0.5))


    (.addActionListener load-btn
                        (proxy [java.awt.event.ActionListener] []
                          (actionPerformed [_]
                            (let [points (csv/load-points-via-dialog)]
                              (if (and points (seq points))
                                (do
                                  (swap! app-state assoc :points points) ;save state
                                  (.setText info-label (dataset-info points)) ;update label
                                  (.setVisible visualize-btn (is-2d? points))) ;show visualize button only if 2D

                                ; if user canceled or empty file
                                (do
                                  (.setText info-label "No dataset loaded")
                                  (.setVisible visualize-btn false)))))))

    (.addActionListener visualize-btn
                        (proxy [java.awt.event.ActionListener] []
                          (actionPerformed [_]
                            (let [points (:points @app-state)]
                              (when (and points (seq points))
                                (visual/start points))))))

    ; label center
    (.setAlignmentX info-label 0.5)

    ; hide visualize initially
    (.setVisible visualize-btn false)

    ; ===== LAYOUT =====
    (.add panel (Box/createRigidArea (Dimension. 0 10)))
    (.add panel load-btn)

    (.add panel (Box/createRigidArea (Dimension. 0 20)))
    (.add panel info-label)

    (.add panel (Box/createRigidArea (Dimension. 0 20)))
    (.add panel visualize-btn)

    ; center panel inside root
    (.add root (Box/createVerticalGlue))
    (.add root panel)
    (.add root (Box/createVerticalGlue))

    ; ===== FRAME =====
    (.add frame root)
    (.setSize frame 500 350)
    (.setLocationRelativeTo frame nil)
    (.setDefaultCloseOperation frame JFrame/EXIT_ON_CLOSE)
    (.setVisible frame true)))