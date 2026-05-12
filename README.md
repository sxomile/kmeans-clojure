# Clojure K-Means implementation

## Table of contents

- [Overview](#overview)
- [About K-Means Clustering](#about-k-means-clustering)
- [Running the application](#running-the-application)
- [Dataset format](#dataset-format)
- [Application Interface Overview](#application-interface-overview)
- [Application Workflow](#application-workflow)
  - [Edge cases](#edge-cases)
- [Implementation process](#implementation-process)
- [Project structure](#project-structure)
- [Tech Stack Overview](#tech-stack-overview)
- [Challenges during development](#challenges-during-development)
- [Performance considerations](#performance-considerations)
- [Known limitations](#known-limitations)
- [Possible future improvements](#possible-future-improvements)

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

## Running the application

1. Clone repository to the local machine
2. Install Leiningen
3. Run command: **lein run**

To run tests, use command: **lein midje**

## Dataset Format

Datasets must be provided in CSV format with headers. All values inside dataset are expected to be numeric.

Example of valid 2D dataset:

x,y

1.0,2.0

2.5,3.1

4.2,1.9

Example of valid 3D dataset:

x,y,z

1.0,2.0,3.0

4.1,5.2,6.3

Each row represents one point, while each column represents one dimension.

## Application interface overview

Interface looks like simple java swing form:

<img src="readme_img/interace_preview.png" width="30%" height="30%">

Form consists of the following elements:

- K input field -> input the number of wanted clusters;
- Load CSV buton -> enables user to load csv file from local machine;
- Run K-Means -> runs the algorithm on the loaded dataset;
- Message label -> writes messages for user to make it easier to understand if something's happening or there is some issue;
- Text area -> outputs details of the algorithm run;
- Hidden "Visualize" button -> shown only when 2d dataset is loaded. Displays animation of the algorithm and enables user to move through each iteration of K-Means. Complete form with hidden button will be shown in image below.

<img src="readme_img/interface_preview_full.png" width="30%" height="30%">

## Application Workflow

When user runs app, the form shows up:

<img src="readme_img/interace_preview.png" width="30%" height="30%">

In the input field user chooses the number of centroids they want. In this example I will keep 3. This data can be modified at any moment.

In order to run the algorithm, user needs to click "Load CSV" button to load data. Clicking the buttons opens swing-style window that enables them to select file from their local machine:

<img src="readme_img/csv_load.png" width="30%" height="30%">

Since loaded data is 2-dimensional, the "Visulaize" button shows up. The label informs user that 300 points are loaded, and that data is 2d.

<img src="readme_img/interface_preview_full.png" width="30%" height="30%">

By clicking "Run K-Means" button, the algorithm is run and details are displayed in text area.

<img src="readme_img\algorithm_run_2d.png" width="30%" height="30%">

User can now use visualize button as well to see the animation of the algo run. By using arrow keys, user can also navigate through each step of the animation, which makes it easier to follow what happens.

Example of animation and navigation through steps:

<img src="blob/visualization.gif" width="50%" height="50%">

If the data is n-dimensional, where n is whole number and n > 2, algorithm details will be displayed, but visualization will be disabled.

Example of 3d case:

<img src="readme_img/algorithm_run_3d.png" width="30%" height="30%">

### Edge cases

1. User uses invalid data in input field:

<img src="readme_img/invalid_k.png" width="30%" height="30%">

2. User tries to run algorithm before loading anything:

<img src="readme_img/no_dataset.png" width="30%" height="30%">

3. User tries to load non-csv type of file -> this edge case is covered in way that explorer won't display anything but csv files for user.

4. User tries to visualize data before running the algorithm:

<img src="readme_img/visualize_before_running.png" width="30%" height="30%">

## Implementation process

Application was developed in 4 stages:

1. Development of algorithm logic
2. Development of CSV import
3. Development of visuals/animaions
4. Development of UI

During development, some previously completed stages had to be modified to ensure proper integration with the rest of the application. However, this was the general order of implementation.

### 1. Development of algorithm logic

Algorithm logic development was completely **test-driven**, using **Midje** framework. This means that the tests were written before any specific function, and then the functions would be checked without need to run them in repl.

Algorithm logic is implemented inside of kmeans.clj file, and consists of 11 pure functions:

1. distance -> calculates euclidean distance between 2 points;
2. nearest-centroid -> assigns a point to the nearest centroid;
3. assign-clusters -> groups points in their nearest centroid, uses nearest-centroid as helper;
4. mean -> calculates mean, useful for recalculating centroids;
5. mean-point -> calculates mean of specific points, uses mean as helper;
6. recompute-centroids -> recomputes centroids;
7. converged? -> checks if algorithm converged;
8. init-centroids -> initializes centroids for the first iteration;
9. kmeans-step -> completes one step of K-Means algorithm (assign, then recompute);
10. kmeans -> orchestrates all of the functions for one complete run of algorithm. Currently unused because it doesn't quite fit what we need to pass to the UI, but was useful to see how algo works before UI was present;
11. kmeans-with-history -> almost the same as kmeans, but keeps track of historical data and is compatible with UI.

Tests are inside of kmeans_test.clj file and can be run in terminal through command **lein midje**.

### 2. Development of CSV import

This phase started off as test-driven, but as soon as java.io was present and functions stopped being pure it didn't seems to make sense. The dialogs couldn't even be run from REPL, no matter what I tried, so I decided to manually test the rest of the logic via console, by directly running the app.

Logic is inside of csv.clj file, and consists of 5 functions:

1. parse-row -> parses vector of numbers to double;
2. csv->points -> parses csv file to set of points;
3. load-points-from-file -> loads points directly from given file;
4. choose-csv-file -> opening dialog that enables user to only be able to select csv files;
5. load-points-via-dialog -> integrates all the functions above to enable user to select csv via dialog and load it in application.

Tests are written in csv_test file and can be run in terminal through command **lein midje**.

### 3. Development of visuals/animaions

Logic for this segment was developed in visual.clj file. This file went through the biggest amount of changes since initial development, after the UI was present.

Visuals are developed using **quil** library. The file is formatted in a similar fashion as it is displayed on quil github page, with many different helper functions added or removed along the way.

Each iteration of the algorithm is stored as historical state data containing centroid positions and cluster assignments.

Visualization replays this stored history frame-by-frame, allowing the user to observe how clusters evolve during convergence.

In the initial version visuals worked with some random points, then it worked with actual kmeans algorithm. After UI was added, there was an issue where UI ran its own K-Means, while visuals run K-Means of their own, so later visuals were completely redefined to display just historical data that it is given from UI. Visuals use atoms to keep track of the variable states.

Visual.clj consists of 8 functions:

1. random-color -> defines random color. Centroids are each given some random color;
2. bounds -> defines bounds of visual screen. Useful for scaling points from their regular value to size of that window;
3. scale-history -> scales historical data to the window size;
4. key-pressed -> updates different states based on key pressed. Useful for navigating through steps of animation;
5. setup -> defines setup. Standard practice in quil;
6. draw -> defines how function will draw points, also standard part of quil;
7. start -> starts animation, regular part of quil.

### 4. Development of UI

UI is developed using Java Swing library. UI code is in ui.clj file, and is invoked in main function, which is in core.clj.

Even though UI code seems relatively big, it mostly consists of element ordering and design settings, along with consumption of functions from above defined files. For this reason, I will not list functions for this part.

It keeps the state of history data, which it gets from kmeans-with-history, and k variable, which it gets from input field on the form.

## Project Structure

Project is organized into several core files and directories, out of which essential are:

- kmeans-clojure
  - src
    - kmeans-clojure
      - core.clj -> application entry point
      - ui.clj -> Swing user interface
      - kmeans.clj -> K-Means algorithm implementation
      - visual.clj -> visualization and animation logic
      - csv.clj -> CSV import and parsing
  - test
    - kmeans-clojure
      - kmeans_test.clj -> tests for algorithm logic
      - csv_test.clj -> tests for CSV loading logic (specifically parsing data)

## Tech stack overview

The application is built using the following technologies and libraries:

- Clojure -> core programming language;
- Leiningen -> project management and dependency handling;
- Quil -> visualization and animation of K-Means iterations;
- Java Swing -> graphical user interface (GUI)
- Midje -> test-driven development and unit testing
- clojure.data.csv -> CSV parsing and dataset loading
- java.io -> file handling

## Challenges During Development

Several issues appeared during development, some of which are:

- Synchronizing UI state with visualization state
- Passing historical algorithm data to visualization
- Scaling data points correctly for different datasets
- Separating visualization logic from algorithm logic

The biggest challenge was redesigning visualization so it would no longer execute its own K-Means logic independently from the UI.

## Performance Considerations

Current implementation works well for smaller and medium-sized datasets.

For very large datasets, performance decreases because:
- every iteration recalculates distances for all points
- visualization stores complete iteration history
- rendering large number of points becomes slower

Possible future optimization could include parallel processing or reducing stored history data.

## Known Limitations

- Visualization currently supports only 2D datasets
- Initial centroid selection is random
- Large datasets can reduce visualization performance
- No dataset normalization is currently implemented
- UI design is intentionally simple and utility-focused

## Possible Future Improvements

Potential future improvements for the application include:

- Interactive visualization controls through UI buttons instead of keyboard navigation
- More detailed representation of data on visual screen
- Real-time centroid dragging and manipulation
- Improved visualization styling and animations
- Dataset normalization and preprocessing support
- Automatic K selection methods, like Elbow Method
- Exporting clustering results to CSV
- Better optimized handling for large datasets
- 3D visualization support
- N-dimensional visualization support (comparing 2-3 desired variables)
- Displaying statistical metrics for clustering quality