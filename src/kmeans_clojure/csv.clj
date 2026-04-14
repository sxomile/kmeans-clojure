(ns kmeans-clojure.csv
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io])
  (:import [javax.swing JFileChooser]
           [javax.swing.filechooser FileNameExtensionFilter]))

;; parse one csv row into a vector of doubles
(defn parse-row [row]
  (mapv #(Double/parseDouble %) row))

;; read CSV reader into vector of numeric points
(defn csv->points
  ([reader] (csv->points reader true))
  ([reader has-header?]
   (let [rows (csv/read-csv reader)
         data-rows (if has-header? (rest rows) rows)]
     (mapv parse-row data-rows))))

;; load points directly from file
(defn load-points-from-file
  ([file] (load-points-from-file file true))                ;assuming files will always have header
  ([file has-header?]
   (with-open [reader (io/reader file)]
     (let [data (csv->points reader has-header?)]
       (println "Loaded points:")
       (doseq [row data]
         (println row))                                     ;in order to check if it loads data properly
       data))))

;; open file chooser dialog for selecting CSV file, making sure only csv can be selected
(defn choose-csv-file []
  (let [chooser (JFileChooser.)
        filter (FileNameExtensionFilter.
                 "CSV files"
                 (into-array String ["csv"]))]
    (.setFileFilter chooser filter)
    (when (= (.showOpenDialog chooser nil)
             JFileChooser/APPROVE_OPTION)
      (.getSelectedFile chooser))))

;; load CSV points through file chooser dialog
(defn load-points-via-dialog
  ([] (load-points-via-dialog true))
  ([has-header?]
   (let [file (choose-csv-file)]
     (if (nil? file)
       nil
       (load-points-from-file file has-header?)))))