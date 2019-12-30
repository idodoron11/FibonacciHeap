import java.util.Iterator;

/**
 * FibonacciHeap
 * <p>
 * An implementation of fibonacci heap over integers.
 */
public class FibonacciHeap {

    /**
     * public boolean isEmpty()
     * <p>
     * precondition: none
     * <p>
     * The method returns true if and only if the heap
     * is empty.
     */
    private HeapNode min; // Pointer to the minimal node in the heap.
    // The min is assumed to be the "first" tree in the circular list of trees.
    private int size; // How many nodes are in this heap.
    private static int totalLinks = 0;
    private static int totalCuts = 0;
    private int totalTrees = 0;
    private int totalMarkedTrees = 0;

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

    public boolean isEmpty() {
        return (this.min == null || this.size == 0);
    }

    /**
     * public HeapNode insert(int key)
     * <p>
     * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
     */
    public HeapNode insert(int key) {
        HeapNode node = new HeapNode(key);
        return insert(node);
    }

    private void totalTreesInspector(int delta, String reason) {
        int before = this.totalTrees;
        this.totalTrees += delta;
        String message = "Number of trees changed from " + before + " to " + this.totalTrees + "\n" +
                "as a result of \"" + reason + "\".";
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
     * public void deleteMin()
     * <p>
     * Delete the node containing the minimum key.
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
            /**
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
     * @param A
     * @param B
     * @pre A != null && B != null
     * @pre !A.isEmpty() && !B.isEmpty()
     * @pre A.findMin().getKey() < B.findMin().getKey() || A.findMin().getKey() > B.findMin().getKey()
     * @pre A.getParent() == null && B.getParent() == null
     * @pre A.getRank() == B.getRank()
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
        totalTreesInspector(-1, "two trees had been linked into one.");
        return min;
    }

    /**
     * Consolidates the list of trees in this heap, such that
     * there the heap does not have two trees of the same rank anymore.
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
                HeapNode mergedTree = linkTrees(tree, existingTree);
                tree = mergedTree;
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
     * public HeapNode findMin()
     * <p>
     * Return the node of the heap whose key is minimal.
     */
    public HeapNode findMin() {
        return this.min;
    }

    /**
     * public void meld (FibonacciHeap heap2)
     * <p>
     * Meld the heap with heap2
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
     * public int size()
     * <p>
     * Return the number of elements in the heap
     */
    public int size() {
        return this.size;
    }

    /**
     * public int[] countersRep()
     * <p>
     * Return a counters array, where the value of the i-th entry is the number of trees of order i in the heap.
     */
    public int[] countersRep() {
        int length = (int) Math.ceil(Math.log(this.size + 1) / Math.log(2));
        int[] ranksCount = new int[length];
        // This is a good opportunity to re-count the number of trees in the heap,
        // without increasing time complexity order.
        totalTreesInspector(this.totalTrees, "re-coount during countersRep()");
        this.totalTrees = 0;
        for (Iterator<HeapNode> iter = this.min.iterator(); iter.hasNext(); ) {
            HeapNode node = iter.next();
            totalTreesInspector(1, "A new tree was found during countersRep()");
            int nodeRank = node.rank;
            ranksCount[nodeRank]++;
        }
        return ranksCount;
    }

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
        if (rootRank == 0 || root.child == null) {
            // Nothing.
        } else {
            for (Iterator<HeapNode> iter = root.child.iterator(); iter.hasNext(); ) {
                HeapNode node = iter.next();
                ranksCount = countAllRanksHelper(ranksCount, node);
            }
        }
        ranksCount[rootRank]++;
        return ranksCount;
    }

    /**
     * public void delete(HeapNode x)
     * <p>
     * Deletes the node x from the heap.
     */
    public void delete(HeapNode x) {
        int delta = x.getKey() - this.min.getKey() + 1; // >= 0
        decreaseKey(x, delta);
        deleteMin();
    }

    /**
     * public void decreaseKey(HeapNode x, int delta)
     * <p>
     * The function decreases the key of the node x by delta. The structure of the heap should be updated
     * to reflect this chage (for example, the cascading cuts procedure should be applied if needed).
     *
     * @param x
     * @param delta
     * @pre x != null && delta >= 0 && [x.getKey() == k for some 0 <= k]
     * @post x.getKey() == k - delta
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
            if (parent.mark == false) {
                parent.mark = true;
                totalMarkedTrees++;
            } else
                cascadingCut(parent, grandparent);
        }
    }

    /**
     * public int potential()
     * <p>
     * This function returns the current potential of the heap, which is:
     * Potential = #trees + 2*#marked
     * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap.
     */
    public int potential() {
        return totalTrees + 2 * totalMarkedTrees; // should be replaced by student code
    }

    /**
     * public static int totalLinks()
     * <p>
     * This static function returns the total number of link operations made during the run-time of the program.
     * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of
     * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value
     * in its root.
     */
    public static int totalLinks() {
        return totalLinks;
    }

    /**
     * public static int totalCuts()
     * <p>
     * This static function returns the total number of cut operations made during the run-time of the program.
     * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods).
     */
    public static int totalCuts() {
        return totalCuts;
    }

    /**
     * public static int[] kMin(FibonacciHeap H, int k)
     * <p>
     * This static function returns the k minimal elements in a binomial tree H.
     * The function should run in O(k(logk + deg(H)).
     */
    public static int[] kMin(FibonacciHeap H, int k) {
        if (H == null || H.isEmpty() || k > 0)
            return new int[0];
        if (k > H.size())
            k = H.size();
        return new int[0];
    }

    /**
     * public class HeapNode
     * <p>
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

        public HeapNode(int key) {
            this.key = key;
            this.mark = false;
            this.next = this;
            this.prev = this;
        }

        public int getKey() {
            return key;
        }

        public void setKey(int key) {
            this.key = key;
        }

        public int getRank() {
            return rank;
        }

        public boolean isMark() {
            return mark;
        }

        public HeapNode getNext() {
            return this.next;
        }

        public HeapNode getPrev() {
            return this.prev;
        }

        public HeapNode getParent() {
            return parent;
        }

        /**
         * Inserts newLeft between this.getPrev() and this.
         *
         * @param newLeft
         * @pre newLeft doesn't belong to this list.
         * @pre newLeft != null
         * @post newLeft.getNext() == this && newLeft == this.getPrev()
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
         * @param newRight
         * @pre newRight doesn't belong to this list.
         * @pre newRight != null
         * @post this.getNext() == newRight && this == newRight.getPrev()
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
         * @param trees
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
         * @param trees
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

        public HeapNode getRightmostChild() {
            if (this.child == null)
                return null;
            return this.child.prev;
        }

        public HeapNode getLeftmostChild() {
            if (this.child == null)
                return null;
            return this.child;
        }

        /**
         * Returns a new iterator, to iterate over all the nodes at the same depth in the tree.
         * If $this is a root, then the iteration will be over all the roots in the heap.
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
