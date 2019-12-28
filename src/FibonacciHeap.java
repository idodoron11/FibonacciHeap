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
    private HeapNodeList trees;
    private HeapNode min; // Pointer to the minimal node in the heap.
    private int size; // How many nodes are in this heap.
    private static int totalLinks = 0;
    private static int totalCuts = 0;

    public FibonacciHeap() {
        trees = new HeapNodeList();
        min = null;
        size = 0;
    }

    // builds a one node heap.
    protected FibonacciHeap(int key) {
        this();
        trees.insert(key);
        min = trees.getFirst();
        size++;
    }

    public boolean isEmpty() {
        return (this.trees == null || this.min == null || this.size == 0);
    }

    /**
     * public HeapNode insert(int key)
     * <p>
     * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
     */
    public HeapNode insert(int key) {
        FibonacciHeap heap2 = new FibonacciHeap(key);
        HeapNode node = heap2.findMin();
        this.meld(heap2);
        return node;
    }

    /**
     * public void deleteMin()
     * <p>
     * Delete the node containing the minimum key.
     */
    public void deleteMin() {
        HeapNode min = this.findMin();
        this.trees.delete(min);
        HeapNodeList subTrees = min.getChildren();
        this.trees.concat(subTrees);
        consolidate();
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
        HeapNodeList minChildren = min.getChildren();
        minChildren.insertFirst(other);
        other.parent = min;
        min.setRank(min.getRank() + 1);
        return min;
    }

    /**
     * Consolidates the list of trees in this heap, such that
     * there the heap does not have two trees of the same rank anymore.
     */
    private void consolidate() {
        Iterator<HeapNode> iter = trees.iterator();
        int length = (int) Math.ceil(Math.log(this.size) / Math.log(2));
        HeapNode[] bucket = new HeapNode[length];
        HeapNode minimum = null;
        HeapNodeList newTrees = new HeapNodeList();

        HeapNode tree = null;
        while (iter.hasNext()) {
            if (tree == null)
                tree = iter.next();
            int treeRank = tree.getRank();
            HeapNode existingTree = bucket[treeRank];
            if (existingTree == null) {
                bucket[treeRank] = tree;
                if (iter.hasNext())
                    tree = iter.next();
            } else {
                HeapNode mergedTree = linkTrees(tree, existingTree);
                tree = mergedTree;
            }
        }

        for (int i = 0; i < length; i++) {
            HeapNode bucketTree = bucket[i];
            newTrees.insert(bucketTree);
            if (minimum == null || bucketTree.getKey() < minimum.getKey())
                minimum = bucketTree;
        }

        this.min = minimum;
        this.trees = newTrees;
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

        HeapNodeList thisTreeList = this.trees;
        HeapNodeList newTreeList = heap2.trees;
        thisTreeList.concat(newTreeList);
        this.size = this.size + heap2.size;
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
        int[] arr = new int[42];
        return arr; //	 to be replaced by student code
    }

    /**
     * public void delete(HeapNode x)
     * <p>
     * Deletes the node x from the heap.
     */
    public void delete(HeapNode x) {
        return; // should be replaced by student code
    }

    /**
     * public void decreaseKey(HeapNode x, int delta)
     * <p>
     * The function decreases the key of the node x by delta. The structure of the heap should be updated
     * to reflect this chage (for example, the cascading cuts procedure should be applied if needed).
     */
    public void decreaseKey(HeapNode x, int delta) {
        return; // should be replaced by student code
    }

    /**
     * public int potential()
     * <p>
     * This function returns the current potential of the heap, which is:
     * Potential = #trees + 2*#marked
     * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap.
     */
    public int potential() {
        return 0; // should be replaced by student code
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
        return 0; // should be replaced by student code
    }

    /**
     * public static int totalCuts()
     * <p>
     * This static function returns the total number of cut operations made during the run-time of the program.
     * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods).
     */
    public static int totalCuts() {
        return 0; // should be replaced by student code
    }

    /**
     * public static int[] kMin(FibonacciHeap H, int k)
     * <p>
     * This static function returns the k minimal elements in a binomial tree H.
     * The function should run in O(k(logk + deg(H)).
     */
    public static int[] kMin(FibonacciHeap H, int k) {
        int[] arr = new int[42];
        return arr; // should be replaced by student code
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
        protected HeapNodeList children;
        protected HeapNode next;
        protected HeapNode prev;
        protected HeapNode parent;
        protected boolean dummy;

        public HeapNode(int key) {
            this.key = key;
            this.mark = false;
            this.next = this;
            this.prev = this;
            this.dummy = false;
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

        public void setRank(int rank) {
            this.rank = rank;
        }

        public boolean isMark() {
            return mark;
        }

        public void setMark(boolean mark) {
            this.mark = mark;
        }

        public HeapNodeList getChildren() {
            return children;
        }

        public void setChildren(HeapNodeList children) {
            this.children = children;
        }

        public void setDummy(boolean dummy) {
            this.dummy = dummy;
        }

        public HeapNode getNext() {
            HeapNode next = this.next;
            // Skip the sentinel.
            if (next.isDummy())
                next = next.next;
            return next;
        }

        public HeapNode getPrev() {
            HeapNode prev = this.prev;
            // Skip the sentinel.
            if (prev.isDummy())
                prev = prev.prev;
            return prev;
        }

        public HeapNode getParent() {
            return parent;
        }

        public void setParent(HeapNode parent) {
            this.parent = parent;
        }

        public HeapNode getRightmostChild() {
            return this.children.getLast();
        }

        public HeapNode getLeftmostChild() {
            return this.children.getFirst();
        }

        public boolean isDummy() {
            return dummy;
        }

        /**
         * Returns a new iterator, to iterate over all the nodes at the same depth in the tree.
         * If $this is a root, then the iteration will be over all the roots in the heap.
         */
        public Iterator<HeapNode> getListIterator() {
            HeapNode start;
            if (this.isDummy())
                start = this.getNext();
            else
                start = this;
            return new Iterator<HeapNode>() {
                HeapNode current = start;
                HeapNode prev = null;
                boolean firstCycle = true;

                @Override
                public boolean hasNext() {
                    if (current.isDummy())
                        next();
                    return firstCycle;
                }

                @Override
                public HeapNode next() {
                    prev = current;
                    current = current.getNext();
                    if (current == start)
                        firstCycle = false;
                    return prev;
                }
            };
        }
    }

    public class HeapNodeList {
        protected HeapNode sentinel;
        int length;

        protected HeapNodeList() {
            sentinel = new HeapNode(0);
            sentinel.dummy = true;
            length = 0;
        }

        protected void insert(int key) {
            HeapNode node = new HeapNode(key);
            int rank = 0;
            if (length != 0)
                rank = this.getFirst().getRank();
            node.setRank(rank);
            insert(node);
        }

        protected void insert(HeapNode node) {
            HeapNode last = sentinel.getPrev();
            last.next = node;
            node.next = sentinel;
            sentinel.prev = node;
            if (sentinel.getPrev() == sentinel)
                sentinel.prev = node;
            node.prev = last;
            length++;
        }

        protected void insertFirst(HeapNode node) {
            insertFromLeft(getFirst(), node);
        }

        protected void delete(HeapNode node) {
            if (node.isDummy())
                return;
            HeapNode last = this.sentinel.getPrev();
            HeapNode first = this.sentinel.getNext();
            if (last == node)
                sentinel.prev = last.getPrev();
            if (first == node)
                sentinel.next = first.getNext();
            HeapNode before = node.getPrev();
            HeapNode after = node.getNext();
            before.next = after;
            after.prev = before;
            node.next = null;
            node.prev = null;
            length--;
        }

        protected void concat(HeapNodeList list) {
            HeapNode thisFirst = this.getFirst();
            HeapNode thisLast = this.getLast();
            HeapNode listFirst = list.getFirst();
            HeapNode listLast = list.getLast();

            thisLast.next = listFirst; // Instead of this.sentinel
            listFirst.prev = thisLast; // Instead of list.sentinel
            sentinel.prev = listLast; // Instead of thisLast
            listLast.next = sentinel; // Instead of list.sentinel
            this.length += list.getLength();

            // We don't want list to be used later.
            list.sentinel.next = null;
            list.sentinel.prev = null;
            list.length = 0;
        }

        protected HeapNode getFirst() {
            return this.sentinel.getNext();
        }

        protected HeapNode getLast() {
            return this.sentinel.getPrev();
        }

        protected int getLength() {
            return this.length;
        }

        protected Iterator<HeapNode> iterator() {
            return this.getFirst().getListIterator();
        }

        /**
         * Inserts newLeft between node.getPrev() and node.
         *
         * @param node
         * @param newLeft
         * @pre newLeft doesn't belong to this list.
         * @pre node != null && newLeft != null
         * @post newLeft.getNext() == node && newLeft == node.getPrev()
         */
        protected void insertFromLeft(HeapNode node, HeapNode newLeft) {
            HeapNode oldLeft = node.prev;
            oldLeft.next = newLeft;
            newLeft.next = node;
            newLeft.prev = oldLeft;
            this.length++;
        }

        /**
         * Inserts newLeft between node and node.getRight().
         *
         * @param node
         * @param newRight
         * @pre newRight doesn't belong to this list.
         * @pre node != null && newRight != null
         * @post node.getNext() == newRight && node == newRight.getPrev()
         */
        protected void insertFromRight(HeapNode node, HeapNode newRight) {
            HeapNode oldRight = node.next;
            oldRight.prev = newRight;
            newRight.prev = node;
            newRight.next = oldRight;
            this.length++;
        }
    }
}