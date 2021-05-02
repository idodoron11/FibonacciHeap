# Fibonacci Heap Data Structure

A Fibonacci heap is a priority queue, composed of a list of binomial trees satisfying the minimum-heap property: **the key of a child is always greater than or equal to the key of the parent**. This implies that the minimum key is always at the root of one of the trees (and we maintain a pointer so we can return the minimum in constant time).

 The structure of a Fibonacci heap is flexible. In each tree, in each level, the vertices are ordered arbitrary. The trees do not have a prescribed shape and in the extreme case the heap can have every element in a separate tree. This flexibility allows some operations to be executed in a lazy manner, postponing the work for later operations. In fact, a Fibonacci heap is very similar to a Lazy Binomial Heap, except it also maintains the following invariant: **in every tree, every vertex except the root loses one child at most.** 

Fibonacci heaps are useful for improving asymptotic running time of important algorithms. In particular, they reduce significantly the time complexity of Dijkstra's algorithm for computing the shortest path between two nodes in a graph.

The structure supports the following methods:

| Method       | Description                                                  | W.C. Complexity | Amortized Complexity |
| ------------ | ------------------------------------------------------------ | --------------- | -------------------- |
| Insert       | Inserts a new key to the heap, while maintaining the valid structure of the heap. | $O(1)$          | $O(1)$               |
| Get-Min      | Returns a reference to the vertex with the smallest key.     | $O(1)$          | $O(1)$               |
| Delete-Min   | Deletes the vertex with the smallest key in the heap, while maintaining the valid structure of the heap. | $O(n)$          | $O(\log n)$          |
| Decrease-Key | Decreases the key of a given vertex, while maintaining the valid structure of the heap. | $O(n)$          | $O(1)$               |

