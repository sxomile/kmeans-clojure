(ns kmeans-clojure.csv-test
  (:require [clojure.test :refer :all]
            [midje.sweet :refer [facts => throws]]
            [kmeans-clojure.csv :refer :all]))

(facts "parse-row converts strings to doubles"
      (parse-row ["1" "2.5" "-3"])
      => [1.0 2.5 -3.0])

(facts "parse-row fails on invalid input"
      (parse-row ["1" "abc"])
      => (throws NumberFormatException))

(facts "csv->points skips header"
      (let [reader (java.io.StringReader. "x,y\n1,2\n3,4")]
        (csv->points reader true))
      => [[1.0 2.0] [3.0 4.0]])

(facts "csv->points without header"
      (let [reader (java.io.StringReader. "1,2\n3,4")]
        (csv->points reader false))
      => [[1.0 2.0] [3.0 4.0]])

;tbh not really sure how to write meaningful tests for other functions

;some of them can't even be run in repl, so I have to run the app in order to try them