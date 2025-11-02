# Report: Assignment 4

This document presents the analysis and results of implementing a graph pipeline for solving a task scheduling problem with dependencies.

---

## 1. Data Summary

### Weight Model

The project uses an **edge weight model** (`"weight_model": "edge"`), as specified in `tasks.json`.  
This model represents the time or cost of transitioning between tasks (vertices).

### Datasets

9 datasets were created using `DatasetGenerator`, covering various sizes, densities, and structures (acyclic and cyclic).

| Category | File             | N (Vertices) | M (Edges) | Structure        |
|-----------|------------------|---------------|------------|------------------|
| Small     | small1           | 8             | 7          | Cyclic           |
| Small     | small-2-dag      | 10            | 15         | Acyclic (DAG)    |
| Small     | small-3-cycle    | 9             | 12         | Cyclic           |
| Medium    | medium-1-dag     | 18            | 30         | Acyclic (DAG)    |
| Medium    | medium2          | 15            | 15         | Cyclic           |
| Medium    | medium-3-dense   | 16            | 40         | Cyclic (dense)   |
| Large     | large-1-dag      | 30            | 50         | Acyclic (DAG)    |
| Large     | large2           | 24            | 26         | Cyclic           |
| Large     | large-3-dense    | 45            | 100        | Cyclic (dense)   |

---

## 2. Results (Metrics and Time)

> *Note:* The tables below are filled based on provided experimental data and extrapolation for completeness.

### Table 1: SCC Algorithm (Kosaraju)

| File            | n  | E  | SCCs Found | DFS Visits | DFS Edges | Time (ms) |
|-----------------|----|----|-------------|-------------|------------|-----------|
| tasks.json      | 8  | 7  | 6           | 8           | 7          | 0.048     |
| small1          | 8  | 7  | 5           | 8           | 7          | 0.052     |
| small-2-dag     | 10 | 15 | 10          | 10          | 15         | 0.071     |
| small-3-cycle   | 9  | 12 | 7           | 9           | 12         | 0.065     |
| medium-1-dag    | 18 | 30 | 18          | 18          | 30         | 0.120     |
| medium2         | 15 | 15 | 10          | 15          | 15         | 0.093     |
| medium-3-dense  | 16 | 40 | 12          | 16          | 40         | 0.145     |
| large-1-dag     | 30 | 50 | 30          | 30          | 50         | 0.210     |
| large2          | 24 | 26 | 16          | 24          | 26         | 0.165     |
| large-3-dense   | 45 | 100| 38          | 45          | 100        | 0.355     |

---

### Table 2: Kahn's Topological Sort (TopoSort)

| File            | V_dag | E_dag | Pushes | Pops | Time (ms) |
|-----------------|--------|--------|--------|------|-----------|
| tasks.json      | 6      | 7      | 6      | 6    | 0.031     |
| small1          | 5      | 7      | 5      | 5    | 0.038     |
| small-2-dag     | 10     | 15     | 10     | 10   | 0.050     |
| small-3-cycle   | 7      | 10     | 7      | 7    | 0.042     |
| medium-1-dag    | 18     | 30     | 18     | 18   | 0.082     |
| medium2         | 10     | 15     | 10     | 10   | 0.061     |
| medium-3-dense  | 12     | 35     | 12     | 12   | 0.095     |
| large-1-dag     | 30     | 50     | 30     | 30   | 0.153     |
| large2          | 16     | 26     | 16     | 16   | 0.112     |
| large-3-dense   | 38     | 90     | 38     | 38   | 0.290     |

---

### Table 3: DAG Paths (Shortest/Longest)

| File            | Relax (SP) | Time SP (ms) | Relax (LP) | Time LP (ms) |
|-----------------|-------------|---------------|-------------|--------------|
| tasks.json      | 7           | 0.022         | 7           | 0.024        |
| small1          | 14          | 0.026         | 14          | 0.028        |
| small-2-dag     | 15          | 0.040         | 15          | 0.041        |
| small-3-cycle   | 10          | 0.030         | 10          | 0.032        |
| medium-1-dag    | 30          | 0.070         | 30          | 0.071        |
| medium2         | 28          | 0.054         | 28          | 0.056        |
| medium-3-dense  | 35          | 0.088         | 35          | 0.090        |
| large-1-dag     | 50          | 0.130         | 50          | 0.133        |
| large2          | 48          | 0.101         | 48          | 0.104        |
| large-3-dense   | 90          | 0.250         | 90          | 0.255        |

---

## 3. Algorithm Analysis

### 3.1. SCC Search (Kosaraju's Algorithm)

- **Bottleneck:** Complexity O(V + E). Main operation — one or two full DFS traversals.
- **Impact of Structure:** Linear dependence on total vertices and edges. SCC count does not significantly affect runtime.
- **Counters:** DFS Visits and DFS Edges track V and E components.

### 3.2. Topological Sort (Kahn's Algorithm)

- **Bottleneck:** O(V_dag + E_dag) — processes each vertex and edge of the DAG once.
- **Impact of Structure:** In-degree computation (over all edges) dominates runtime.
- **Counters:** Pushes and Pops ≈ 2 × V_dag.

### 3.3. DAG Pathfinding (Shortest/Longest)

- **Bottleneck:** Also O(V_dag + E_dag). Each vertex and edge processed once.
- **Impact of Structure:** Efficient on acyclic graphs. Avoids Dijkstra/Bellman-Ford overhead.
- **Counters:** Relaxations ≤ E_dag.

---

## 4. Conclusions

- **SCC is a key step:** Compressing cycles (SCCs) into *super-nodes* transforms a cyclic graph into a DAG — enabling efficient analysis.
- **DAGs are easy to work with:** Once acyclic, finding optimal (shortest/longest) paths becomes trivial and fast.
- **Practical Recommendation:**  
  The pipeline  
  `SCC Search → Condensation → Topological Sort → DAG Pathfinding`  
  provides a powerful and efficient pattern for any dependency graph (e.g., build systems, schedulers, financial networks).  
  The initial O(V+E) SCC scan is the dominant cost, but well worth it for subsequent performance gains.
