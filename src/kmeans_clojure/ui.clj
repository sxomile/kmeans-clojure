(ns kmeans-clojure.ui
  (:require [kmeans-clojure.csv :as csv]
            [kmeans-clojure.kmeans :as k]
            [kmeans-clojure.visual :as visual])
  (:import [javax.swing JFrame JButton JLabel JPanel BoxLayout Box BorderFactory]
           [java.awt Dimension Color Font]
           [javax.swing JTextField]
           [javax.swing JTextArea JScrollPane]))

(defonce app-state (atom {:points nil
                          :k 3}))                           ;adding k state

(defn is-2d? [points]
  (and (seq points)
       (= 2 (count (first points)))))                       ;I will show visualize button only if data is 2d

(defn dataset-info [points]
  (if (nil? points)
    "No dataset loaded"
    (str "Points: " (count points)
         " | Dimensions: " (count (first points)))))

(defn parse-k [s]
  (try
    (let [n (Integer/parseInt s)]
      (when (pos? n) n))
    (catch Exception _ nil)))

(defn format-history [history]
  (apply str
         (for [{:keys [iteration centroids clusters]} history]
           (str "Iteration " iteration "\n"
                "Centroids:\n"
                (apply str (map #(str "  " % "\n") centroids))
                "\nClusters:\n"
                (apply str
                       (for [[centroid pts] clusters]
                         (str "  Centroid " centroid "\n"
                              (apply str (map #(str "    " % "\n") pts))
                              "\n")))
                "\n----------------------------------\n\n"))))

(defn create-ui []
  (let [frame (JFrame. "K-Means App")
        root (JPanel.)                                      ; outer panel (background)

        panel (JPanel.)                                     ;inner panel (card)

        load-btn (JButton. "Load CSV")
        run-btn (JButton. "Run K-Means")
        visualize-btn (JButton. "Visualize")
        info-label (JLabel. "No dataset loaded")

        k-field (JTextField. "3")
        k-label (JLabel. "Inser number of clusters (k):")

        text-area (JTextArea.)
        scroll (JScrollPane. text-area)]

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
    (doseq [btn [load-btn run-btn visualize-btn]]
      (.setFocusPainted btn false)
      (.setFont btn (Font. "Arial" Font/BOLD 14))
      (.setBackground btn (Color. 66 133 244)) ; blue
      (.setForeground btn Color/WHITE)
      (.setMaximumSize btn (Dimension. 200 40))
      (.setAlignmentX btn 0.5))

    ; ===== TEXTBOX STYLE =====
    (doto k-field
      (.setMaximumSize (Dimension. 120 35))
      (.setPreferredSize (Dimension. 120 35))
      (.setFont (Font. "Segoe UI" Font/PLAIN 14))
      (.setBackground Color/WHITE)
      (.setForeground (Color. 33 33 33))
      (.setCaretColor (Color. 66 133 244))
      (.setHorizontalAlignment JTextField/CENTER)
      (.setBorder (BorderFactory/createCompoundBorder
                    (BorderFactory/createLineBorder (Color. 200 200 200) 1 true)
                    (BorderFactory/createEmptyBorder 5 10 5 10))))

    (.setAlignmentX k-label 0.5)
    (.setFont k-label (Font. "Arial" Font/PLAIN 14))

    (.setEditable text-area false)
    (.setLineWrap text-area true)
    (.setWrapStyleWord text-area true)
    (.setFont text-area (Font. "Monospaced" Font/PLAIN 12))

    (.setPreferredSize scroll (Dimension. 400 150))

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

    (.addActionListener run-btn
                        (proxy [java.awt.event.ActionListener] []
                          (actionPerformed [_]
                            (let [points (:points @app-state)
                                  k (parse-k (.getText k-field))]
                              (cond
                                (nil? points)
                                (.setText info-label "Load dataset first")

                                (nil? k)
                                (.setText info-label "Invalid K")

                                (> k (count points))
                                (.setText info-label "K too large")

                                :else
                                (let [result (k/kmeans-with-history points k 100)
                                      history (:history result)]
                                  (swap! app-state assoc :history history)
                                  (.setText text-area (format-history history))))))))

    (.addActionListener visualize-btn
                        (proxy [java.awt.event.ActionListener] []
                          (actionPerformed [_]
                            (let [points (:points @app-state)
                                  k (parse-k (.getText k-field))]
                              (when (and points k (is-2d? points))
                                (let [history (:history @app-state)]
                                  (visual/start history)))))))

    ; label center
    (.setAlignmentX info-label 0.5)

    ; K input row
    (.add panel (Box/createRigidArea (Dimension. 0 15)))
    (.add panel k-label)
    (.add panel (Box/createRigidArea (Dimension. 0 5)))
    (.add panel k-field)

    ; hide visualize initially
    (.setVisible visualize-btn false)

    ; ===== LAYOUT =====
    (.add panel (Box/createRigidArea (Dimension. 0 10)))
    (.add panel load-btn)

    (.add panel (Box/createRigidArea (Dimension. 0 15)))
    (.add panel run-btn)

    (.add panel (Box/createRigidArea (Dimension. 0 20)))
    (.add panel info-label)

    (.add panel (Box/createRigidArea (Dimension. 0 20)))
    (.add panel visualize-btn)

    ; center panel inside root
    (.add root (Box/createVerticalGlue))
    (.add root panel)
    (.add root (Box/createVerticalGlue))

    ; panel on bottom for analysis data
    (.add panel (Box/createRigidArea (Dimension. 0 20)))
    (.add panel scroll)

    ; ===== FRAME =====
    (.add frame root)
    (.setSize frame 500 600)
    (.setLocationRelativeTo frame nil)
    (.setDefaultCloseOperation frame JFrame/EXIT_ON_CLOSE)
    (.setVisible frame true)))