(ns kmeans-clojure.ui
  (:import [javax.swing JFrame JLabel]))

(defn create-ui []
  (let [frame (JFrame. "K-Means App")
        label (JLabel. "Hello K-Means")]

    (.add frame label)
    (.setSize frame 300 200)
    (.setDefaultCloseOperation frame JFrame/EXIT_ON_CLOSE)
    (.setVisible frame true)))