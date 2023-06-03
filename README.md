# Dynamic Graphs in Pan-genomics

This repository showcases my work during a summer internship at IIT Roorkee, under the supervision of Dr. Shahbaz Khan, as part of the SPARK Internship programme. I am grateful to Dr. Shahbaz Khan for his continuous support and guidance throughout the project.

## Area of Research

The research focus of this project is the application of dynamic graphs in Pan-genomics. Pan-genomics refers to the entire set of genes within a species and is commonly used in the fields of molecular biology and genetics. In the domain of Bioinformatics, which involves solving biological problems using software tools and information technology, genetic information is often represented in the form of graphs. One of the challenges in this field is genome assembly, where small fragments of DNA sequences are combined to reconstruct the original chromosomes. In this process, determining the parts that must be present in the solution, known as "safe" parts, is an important problem.

## Introduction

### Problem Statement

The problem addressed in this project is the dynamic verification of the safety of a given walk for an edge-covering walk of a strongly connected graph, considering edge insertions.

To incrementally verify safe walks, the project utilizes the "Hydrostructure" model introduced in "The Hydrostructure: A Universal Framework for Safe and Complete Algorithms for Genome Assembly".

### Hydrostructure Model

The Hydrostructure model classifies a given walk 'W' in a strongly connected graph into four categories: sea, cloud, vapor, and river.

- R-(W): All nodes reachable from the end of 'W' without using the start of 'W'.
- R+(W): All nodes reachable from the start of 'W' without using the end of 'W'.
- Vapor: All nodes that are present in both R+(W) and R-(W).
- Cloud: All nodes in R-(W) that are not in vapor.
- Sea: All nodes in R+(W) that are not in vapor.
- River: All remaining nodes that are not in cloud, sea, or vapor.

Where:
- start(W): The first edge of the walk 'W'.
- end(W): The last edge of the walk 'W'.

## Work

The project includes the implementation of different algorithms for dynamic graph verification.

### Static Algorithm

This algorithm computes the Hydrostructure for a given walk in a strongly connected graph using the definitions of cloud, river, vapor, and sea as provided in the research paper. The computation takes linear time complexity O(m), where 'm' represents the number of edges in the graph.

### Trivial Dynamic Algorithm

The trivial dynamic algorithm incrementally verifies the safety of a walk under edge insertions. After each update, it recomputes the Hydrostructure and checks for safety by examining whether it is a bridge-like case (when Vapor(W) is an open path and the river is non-empty) or an avertible case (when Vapor(W) is the entire graph G). This algorithm incurs an overhead of O(m) time complexity.

### Dynamic Algorithm

The dynamic algorithm incrementally verifies the safety of a specified walk in a strongly connected graph using the components of the Hydrostructure. It maintains the Hydrostructure in overall linear time complexity O(m), where 'm' represents the number of edges in the graph.
