import java.util.Iterator;

/**
 * FibonacciHeap
 * <p>
 * An implementation of fibonacci heap over integers.
 */
public class FibonacciHeap {

    private HeapNode min; // Pointer to the minimal node in the heap.
    // The min is assumed to be the "first" tree in the circular list of trees.
    private int size; // How many nodes are in this heap.
    private static int totalLinks = 0;
    private static int totalCuts = 0;
    private int totalTrees = 0;
    private int totalMarkedTrees = 0;

    /**
     * @comp O(1)
     */
    public FibonacciHeap() {
        min = null;
        size = 0;
    }

    // builds a one node heap.
    protected FibonacciHeap(int key) {
        this();
        min = new HeapNode(key);
        totalTreesInspector(1, "first tree insertion");
        size++;
    }

    /**
     * @return true if and only if the heap is empty.
     * @comp O(1)
     */
    public boolean isEmpty() {
        return (this.min == null || this.size == 0);
    }

    /**
     * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
     *
     * @param key a distinct integer key.
     * @return a pointer to the node which was inserted into the heap.
     * @pre The heap does not contain the input key.
     * @comp O(1)
     */
    public HeapNode insert(int key) {
        HeapNode node = new HeapNode(key);
        return insert(node);
    }

    private void totalTreesInspector(int delta, String reason) {
        int before = this.totalTrees;
        this.totalTrees += delta;
        /*String message = "Number of trees changed from " + before + " to " + this.totalTrees + "\n" +
                "as a result of \"" + reason + "\".";*/
    }

    private HeapNode insert(HeapNode node) {
        if (this.isEmpty())
            this.min = node;
        else {
            HeapNode first = this.min;
            HeapNode last = first.prev;
            node.next = first; // node -> first
            first.prev = node; // node <- first
            last.next = node; // last -> node
            node.prev = last; // last <- node
            node.parent = null;

            if (node.getKey() < first.getKey()) // smaller than the current minimum.
                this.min = node;
        }
        totalTreesInspector(1, "new tree insert");
        size++;
        return node;
    }

    /**
     * Deletes the node containing the minimum key.
     *
     * @pre {@literal !isEmpty() && findMin().getKey() == k for some integer k}
     * @post {@literal isEmpty() || findMin().getKey() >= k}
     * @comp O(log n) where n=size(). Amortized cost is O(1).
     */
    public void deleteMin() {
        if (this.isEmpty())
            return;
        HeapNode min = this.findMin();
        HeapNode minPrev = this.min.getPrev();
        HeapNode minNext = this.min.getNext();
        HeapNode minChild = min.child;
        if (minPrev == min && minNext == min) {
            min.child = null;
            min.next = null;
            min.prev = null;
            this.min = minChild;
            size--;
            totalTreesInspector(-1, "the minimum was deleted, so there's one less tree.");
            if (this.isEmpty())
                return;
            Iterator<HeapNode> iter = minChild.iterator();
            HeapNode minimum = null;
            while (iter.hasNext()) {
                HeapNode node = iter.next();
                node.parent = null;
                totalTreesInspector(1, "one of the minimum subtrees was added to the count.");
                if (minimum == null || node.getKey() < minimum.getKey())
                    minimum = node;
            }
            this.min = minimum;
        } else {
            HeapNode subTrees = min.child;
            this.min.next = null;
            this.min.prev = null;
            minPrev.next = minNext;
            minNext.prev = minPrev;
            this.min = minPrev;
            size--;
            totalTreesInspector(-1, "the minimum was deleted, so there's one less tree.");
            totalTreesInspector(min.rank, "the minimum was of rank " + min.rank + " which is also the number of children it had.");
            /*
             * At this point the minimum doesn't necessarily points
             * to th minimal node, but the consolidation is going to
             * fix it.
             */
            this.min.concat(subTrees);
            consolidate();
        }
    }

    /**
     * Given two binomial trees A and B, of the exact same rank R,
     * this function will link them together into a new binomial
     * tree of rank R+1 in O(1).
     *
     * @param A binomial tree of rank r
     * @param B binomial tree of rank r
     * @pre {@literal A != null && B != null, !A.isEmpty() && !B.isEmpty(),
     * A.findMin().getKey() < B.findMin().getKey() || A.findMin().getKey() > B.findMin().getKey(),
     * A.getParent() == null && B.getParent() == null,
     * A.getRank() == B.getRank()}
     * @post {@literal (A.getLeftmostChild() == B && A.getRank() == r+1 && A.getParent() == null) || (B.getLeftmostChild() == A && B.getRank() == r+1 && B.getParent() == null)}
     * @comp O(1)
     * @
     */
    private HeapNode linkTrees(HeapNode A, HeapNode B) {
        int keyA = A.getKey();
        int keyB = B.getKey();
        HeapNode min, other;
        if (keyA < keyB) {
            min = A;
            other = B;
        } else {
            min = B;
            other = A;
        }
        // At this point we can assume min.getKey() is smaller than other.getKey().
        HeapNode minChildren = min.getLeftmostChild();
        if (minChildren == null) {
            min.child = other;
            other.next = other;
            other.prev = other;
        } else
            minChildren.insertFromLeft(other);
        min.child = other;
        other.parent = min;
        min.rank = min.getRank() + 1;
        totalLinks++;
        min.parent = null;
        totalTreesInspector(-1, "two trees had been linked into one.");
        return min;
    }

    /**
     * Consolidates the list of trees in this heap, such that
     * the heap does not have two trees of the same rank anymore.
     *
     * @pre The heap contains the following keys (key 1,...,key n).
     * @pre The heap is constructed by the following list of independent binomial trees (T1,...,Tk)
     * @pre m is the binary representation of k, s.t. the MSB is the rightmost bit.
     * @pre m=m(1)m(2)...m(j), j = Math.Ceil(Math.log(k+1)/Math.log(2))
     * @post The heap contains the following keys (key 1,...,key n).
     * @post The heap is constructed by a list of binomial trees (B1,...,Bj) s.t. Bi != null iff m(i) != 0
     * @post this.min.getKey() <= Bi.getKey() for all B1,...,Bj
     * @comp O(log n) where n=size()
     */
    private void consolidate() {
        Iterator<HeapNode> iter = this.min.iterator();
        int length = (int) Math.ceil(Math.log(this.size + 1) / Math.log(2));
        HeapNode[] bucket = new HeapNode[length];
        HeapNode minimum = null;
        HeapNode newTrees = null;

        HeapNode tree = null;
        if (iter.hasNext())
            tree = iter.next();
        while (tree != null) {
            int treeRank = tree.getRank();
            HeapNode existingTree = bucket[treeRank];
            if (existingTree == null) {
                bucket[treeRank] = tree;
                if (iter.hasNext())
                    tree = iter.next();
                else
                    tree = null;
            } else {
                bucket[treeRank] = null;
                tree = linkTrees(tree, existingTree);
            }
        }

        totalTreesInspector(-totalTrees, "Trees re-count during consolidation.");
        totalTrees = 0; // This is a good opportunity to re-count the number of trees without increasing runtime complexity.
        for (int i = 0; i < length; i++) {
            HeapNode bucketTree = bucket[i];
            if (bucketTree == null)
                continue;
            if (newTrees == null) {
                newTrees = bucketTree;
                bucketTree.next = bucketTree;
                bucketTree.prev = bucketTree;
            } else {
                HeapNode firstItem = newTrees;
                HeapNode lastItem = newTrees.prev;
                lastItem.next = bucketTree;
                bucketTree.prev = lastItem;
                bucketTree.next = firstItem;
                firstItem.prev = bucketTree;
            }
            bucketTree.parent = null;
            if (minimum == null || bucketTree.getKey() < minimum.getKey())
                minimum = bucketTree;
            totalTreesInspector(1, "counted during consolidation.");
        }

        this.min = minimum;
    }

    /**
     * @return the node of the heap whose key is minimal.
     */
    public HeapNode findMin() {
        return this.min;
    }

    /**
     * Melds the heap with heap2
     *
     * @param heap2 the heap being merged
     * @pre {@literal heap2 != null && !heap2.isEmpty()}
     * @post this.getLeftmostChild().getPrev() == heap2.getRightmostChild()
     * @post this.getRightmostChild().getNext() == heap2.getLeftmostChild()
     * @post this.getRightmostChild().getNext() == heap2.getLeftmostChild()
     * @post this.getRightmostChild() == heap2.getLeftmostChild().getPrev()
     * @comp O(1)
     */
    public void meld(FibonacciHeap heap2) {
        // First, we have to update the pointer to the minimum node.
        if (heap2 == null || heap2.isEmpty()) // heap2 is empty.
            return;
        if (this.isEmpty()) {
            this.min = heap2.min;
            this.size = heap2.size;
            return;
        }
        HeapNode minNode1 = this.findMin();
        HeapNode minNode2 = heap2.findMin();
        HeapNode newMin;
        if (minNode1 == null)
            newMin = minNode2;
        else {
            int minKey1 = minNode1.getKey();
            int minKey2 = minNode2.getKey();
            if (minKey1 > minKey2)
                newMin = minNode2;
            else
                newMin = minNode1;
        }

        HeapNode thisTreeList = this.min;
        HeapNode newTreeList = heap2.min;
        thisTreeList.concat(newTreeList);
        this.size += heap2.size;
        totalTreesInspector(heap2.totalTrees, "melding with another heap.");
        this.totalMarkedTrees += heap2.totalMarkedTrees;
        this.min = newMin;
    }

    /**
     * @return the number of elements in the heap
     * @comp O(1)
     */
    public int size() {
        return this.size;
    }

    /**
     * @return a counters array, where the value of the i-th entry is the number of trees of order i in the heap.
     * @comp O(number of trees), which is O(n) in worst case, and Ω(log n) if the heap is consolidated. (n=size())
     */
    public int[] countersRep() {
        int length = (int) Math.ceil(Math.log(this.size + 1) / Math.log(2));
        int[] ranksCount = new int[length];
        // This is a good opportunity to re-count the number of trees in the heap,
        // without increasing time complexity order.
        totalTreesInspector(this.totalTrees, "re-count during countersRep()");
        this.totalTrees = 0;
        for (Iterator<HeapNode> iter = this.min.iterator(); iter.hasNext(); ) {
            HeapNode node = iter.next();
            totalTreesInspector(1, "A new tree was found during countersRep()");
            int nodeRank = node.rank;
            ranksCount[nodeRank]++;
        }
        return ranksCount;
    }

    /**
     * @return {@literal a int array ranksCount, s.t. for each index 0<=i<=ranksCount.length-1: ranksCount[i] == the number of trees and subtrees of rank i in the heap.}
     */
    public int[] countAllRanks() {
        int length = (int) Math.ceil(Math.log(this.size + 1) / Math.log(2));
        int[] ranksCount = new int[length];
        // This is a good opportunity to re-count the number of trees in the heap,
        // without increasing time complexity order.
        this.totalTrees = 0;
        for (Iterator<HeapNode> iter = this.min.iterator(); iter.hasNext(); ) {
            HeapNode node = iter.next();
            totalTrees++;
            countAllRanksHelper(ranksCount, node);
        }
        return ranksCount;
    }

    private int[] countAllRanksHelper(int[] ranksCount, HeapNode root) {
        int rootRank = root.getRank();
        if (rootRank != 0 && root.child != null) {
            for (Iterator<HeapNode> iter = root.child.iterator(); iter.hasNext(); ) {
                HeapNode node = iter.next();
                ranksCount = countAllRanksHelper(ranksCount, node);
            }
        }
        ranksCount[rootRank]++;
        return ranksCount;
    }

    /**
     * Deletes the node x from the heap.
     *
     * @param x the node being removed from the heap.
     * @pre x is one of this heap's nodes.
     * @post x is no longer in the heap.
     * @comp O(log n)
     */
    public void delete(HeapNode x) {
        int delta = x.getKey() - this.min.getKey() + 1; // >= 0
        decreaseKey(x, delta);
        deleteMin();
    }

    /**
     * The function decreases the key of the node x by delta.
     *
     * @param x     the node whose key is being decreased.
     * @param delta the size of decrement.
     * @pre {@literal x != null && delta >= 0 && [x.getKey() == k for some 0 <= k]}
     * @post {@literal x.getKey() == k - delta}
     * @comp O(log n)
     */
    public void decreaseKey(HeapNode x, int delta) {
        if (delta <= 0 || x == null)
            return;

        x.key = x.key - delta; // So simple, so dangerous... This is the source of all troubles.
        HeapNode parent = x.parent;
        if (parent == null || parent.key <= x.key) {
            if (x.key < this.min.key)
                this.min = x;
            return;
        }

        cascadingCut(x, parent);
    }

    private void cut(HeapNode x, HeapNode parent) {
        x.parent = null;
        if (x.mark)
            totalMarkedTrees--;
        x.mark = false;
        parent.rank -= 1;
        HeapNode xNext = x.next;
        HeapNode xPrev = x.prev;
        if (xNext == x)
            parent.child = null;
        else {
            parent.child = xNext;
            xPrev.next = xNext; // prev ---(x)---> next
            xNext.prev = xPrev; // prev <---(x)--- next
        }
        totalTreesInspector(0, "A new tree was cut, so it will be inserted very shortly.");
        this.insert(x);
        size--; // Insert automatically increments heap's size, but in this case it is unnecessary.
        totalCuts++;
        // There's no need to increment totalTrees, because Insert already does.
    }

    private void cascadingCut(HeapNode x, HeapNode parent) {
        cut(x, parent);
        HeapNode grandparent = parent.parent;
        if (grandparent != null) {
            if (!parent.mark) {
                parent.mark = true;
                totalMarkedTrees++;
            } else
                cascadingCut(parent, grandparent);
        }
    }

    /**
     * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap.
     *
     * @return the current potential of the heap, which is: #trees + 2*#marked
     * @comp O(1)
     */
    public int potential() {
        return totalTrees + 2 * totalMarkedTrees; // should be replaced by student code
    }

    /**
     * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of
     * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value
     * its root.
     *
     * @return the total number of link operations made during the run-time of the program.
     * @comp O(1)
     */
    public static int totalLinks() {
        return totalLinks;
    }

    /**
     * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods).
     *
     * @return the total number of cut operations made during the run-time of the program.
     * @comp O(1)
     */
    public static int totalCuts() {
        return totalCuts;
    }

    /**
     * The function should run in O(k(logk + deg(H)).
     *
     * @param H a single-tree binomial heap of rank r.
     * @param k the number of keys fetched from the tree.
     * @return the k minimal elements in a binomial tree H.
     * @pre {@literal H != null && !H.isEmpty() && k >= 0}
     * @comp O(k ( logk + deg ( H))) where deg(H)=H.findMin().getRank()
     */
    public static int[] kMin(FibonacciHeap H, int k) {
        if (H == null || H.isEmpty() || k <= 0)
            return new int[0];
        HeapNode tree = H.findMin();
        int rank = tree.getRank();
        int size = H.size();
        size = (int) Math.min(size, Math.pow(2, rank));
        if (k > size)
            k = size;
        int[] result = new int[k];
        FibonacciHeap heap = extractToHeap(tree, k); // This process takes O(n) at most.
        // If the heap contains r keys, than the amortized cost of deleteMin() is O(log r).
        // Thus, the total amortized cost of the loop is O(2k) + O(k(log r)) = O(k(log r)) <= O(k(log n))
        for (int i = 0; i < result.length && !heap.isEmpty(); i++) {
            int minimum = heap.findMin().getKey();
            heap.deleteMin();
            result[i] = minimum;
        }

        return result;
    }

    private static FibonacciHeap extractToHeap(HeapNode node, int k) {
        FibonacciHeap heap = new FibonacciHeap();
        extractToHeap(node, heap, k);
        return heap;
    }

    /**
     * @param node the root of the binomial tree.
     * @return A new heap, with copies of the keys node holds.
     */
    public static FibonacciHeap extractToHeap(HeapNode node) {
        int k = (int) Math.pow(2, node.getRank());
        return extractToHeap(node, k);
    }

    private static void extractToHeap(HeapNode node, FibonacciHeap heap, int k) {
        if (node == null || heap == null)
            return;
        HeapNode parent = node.getParent();
        int numberOfItemsInThisDepth = 1;
        if (parent != null)
            numberOfItemsInThisDepth = parent.getRank();
        int heapSizeAfter = heap.size() + numberOfItemsInThisDepth;
        for (Iterator<HeapNode> iter = node.iterator(); iter.hasNext(); ) {
            HeapNode next = iter.next();
            heap.insert(next.getKey());
            if (heapSizeAfter < k && next.child != null)
                extractToHeap(next.child, heap, k);
        }
    }

    /**
     * If you wish to implement classes other than FibonacciHeap
     * (for example HeapNode), do it in this file, not in
     * another file
     */
    public class HeapNode {

        protected int key;
        protected int rank; // number of children
        protected boolean mark;
        protected HeapNode child;
        protected HeapNode next;
        protected HeapNode prev;
        protected HeapNode parent;

        /**
         * @param key the new node's desired key.
         * @comp O(1)
         */
        public HeapNode(int key) {
            this.key = key;
            this.mark = false;
            this.next = this;
            this.prev = this;
        }

        /**
         * @return The key held by this node.
         * @comp O(1)
         */
        public int getKey() {
            return key;
        }

        /**
         * @return The rank of this node.
         * @comp O(1)
         */
        public int getRank() {
            return rank;
        }

        /**
         * @return True iff this node is marked.
         * @comp O(1)
         */
        public boolean isMark() {
            return mark;
        }

        /**
         * @return The next node in the circular HeapNode linked list.
         * @comp O(1)
         */
        public HeapNode getNext() {
            return this.next;
        }

        /**
         * @return The previous node in the circular HeapNode linked list.
         * @comp O(1)
         */
        public HeapNode getPrev() {
            return this.prev;
        }

        public HeapNode getParent() {
            return parent;
        }

        /**
         * Inserts newLeft between this.getPrev() and this.
         *
         * @param newLeft the node being attached to the left of this node.
         * @pre {@literal newLeft doesn't belong to this list && newLeft != null}
         * @post {@literal newLeft.getNext() == this && newLeft == this.getPrev()}
         * @comp O(1)
         */
        protected void insertFromLeft(HeapNode newLeft) {
            HeapNode oldLeft = this.prev;
            oldLeft.next = newLeft;
            newLeft.next = this;
            newLeft.prev = oldLeft;
            newLeft.parent = this.parent;
            this.prev = newLeft;
        }

        /**
         * Inserts newLeft between this and this.getRight().
         *
         * @param newRight the node being attached to the right of this node.
         * @pre {@literal newRight doesn't belong to this list && newRight != null}
         * @post {@literal this.getNext() == newRight && this == newRight.getPrev()}
         * @comp O(1)
         */
        protected void insertFromRight(HeapNode newRight) {
            HeapNode oldRight = this.next;
            oldRight.prev = newRight;
            newRight.prev = this;
            newRight.next = oldRight;
            newRight.parent = this.parent;
            this.next = newRight;
        }

        /**
         * Merges two lists of binomial trees.
         * trees list is placed after trees list.
         * Does not change parent property of any node.
         * The assumption is that none of the nodes have a parent.
         * <p>
         * It does not modify the size property of this heap.
         *
         * @param trees the list of trees being concatenated after this node.
         * @pre trees != null
         * @post Trees list is linked to the end of this list in a circular fashion, when this node is considered as the first item in the list.
         * @comp O(1)
         */
        protected void concat(HeapNode trees) {
            if (trees == null)
                return;
            HeapNode thisFirst = this;
            HeapNode thisLast = this.prev;
            HeapNode listFirst = trees;
            HeapNode listLast = trees.prev;

            thisLast.next = listFirst; // FL -> LF
            listFirst.prev = thisLast; // FL <- LF
            listLast.next = thisFirst; // LL -> FF
            thisFirst.prev = listFirst; // LL <- FF
        }

        /**
         * Merges two lists of binomial trees.
         * this list is placed after tress list.
         * Does not change parent property of any node.
         * The assumption is that none of the nodes have a parent.
         * <p>
         * It does not modify the size property of this heap.
         *
         * @param trees the list of trees being concatenated before this node.
         * @post This list is linked to the end of trees list in a circular fashion, when trees node is considered as the first item in the trees list.
         * @comp O(1)
         */
        protected void reverseConcat(HeapNode trees) {
            if (trees == null)
                return;
            HeapNode thisFirst = this;
            HeapNode thisLast = this.prev;
            HeapNode listFirst = trees;
            HeapNode listLast = trees.prev;

            listLast.next = thisFirst; // FL -> LF
            thisFirst.prev = listLast; // FL <- LF
            thisLast.next = listFirst; // LL -> FF
            listFirst.prev = thisFirst; // LL <- FF
        }

        /**
         * @return the last child of this node (rightmost).
         * @comp O(1)
         */
        public HeapNode getRightmostChild() {
            if (this.child == null)
                return null;
            return this.child.prev;
        }

        /**
         * @return the first child of this node (leftmost).
         * @comp O(1)
         */
        public HeapNode getLeftmostChild() {
            if (this.child == null)
                return null;
            return this.child;
        }

        /**
         * @return a new iterator, to iterate over all the nodes at the same depth in the tree.
         * If $this is a root, then the iteration will be over all the roots in the heap.
         * @comp O(1)
         */
        public Iterator<HeapNode> iterator() {
            HeapNode start;
            start = this;
            return new Iterator<HeapNode>() {
                HeapNode current = start;
                HeapNode prev = null;
                boolean firstCycle = true;

                @Override
                public boolean hasNext() {
                    return firstCycle;
                }

                @Override
                public HeapNode next() {
                    if (!firstCycle)
                        return null;
                    prev = current;
                    current = current.getNext();
                    if (current == start)
                        firstCycle = false;
                    return prev;
                }
            };
        }
    }
}
