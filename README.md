# Clojure K-Means implementation

## Overview

This project is an implementation of the K-Means clustering algorithm written in Clojure.
The application allows users to:
- Load datasets from CSV files
- Run the K-Means clustering algorithm
- Visualize clustering iterations in real time
- Navigate through algorithm steps manually
- Observe centroid movement and cluster formation

The primary goal of the project is to demonstrate the logic behind the K-Means algorithm and functional-style implementation in Clojure.

## About K-Means Clustering

K-Means is an unsupervised machine learning algorithm used for grouping data points into clusters based on similarity.

The algorithm partitions a dataset into k clusters, where each cluster is represented by its centroid (center point).

The process is iterative and consists of the following steps:

1. Initialize centroids
2. Assign each point to the nearest centroid
3. Recalculate centroid positions
4. Repeat from step 2 until convergence

The algorithm attempts to minimize the distance between points and their assigned centroids.

## Application interface and usage overview

Interface looks like simple form with one input field and couple of buttons:

![](C:\Users\PC\dev\dressing\kmeans-clojure\readme_img\interace_preview.png)



