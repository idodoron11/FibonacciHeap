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
        return; // should be replaced by student code

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
        protected HeapNode child;
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

        public HeapNode getChild() {
            return child;
        }

        public void setChild(HeapNode child) {
            this.child = child;
        }

        public HeapNode getNext() {
            return next;
        }

        public void setNext(HeapNode next) {
            this.next = next;
        }

        public HeapNode getPrev() {
            return prev;
        }

        public void setPrev(HeapNode prev) {
            this.prev = prev;
        }

        public HeapNode getParent() {
            return parent;
        }

        public void setParent(HeapNode parent) {
            this.parent = parent;
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
            HeapNode last = sentinel.getPrev();
            last.setNext(node);
            node.setNext(sentinel);
            sentinel.setPrev(node);
            if (sentinel.getPrev() == sentinel)
                sentinel.setPrev(node);
            node.setPrev(last);
            length++;
        }

        protected void delete(HeapNode node) {
            if (node.isDummy())
                return;
            HeapNode last = this.sentinel.getPrev();
            HeapNode first = this.sentinel.getNext();
            if (last == node)
                sentinel.setPrev(last.getPrev());
            if (first == node)
                sentinel.setNext(first.getNext());
            HeapNode before = node.getPrev();
            HeapNode after = node.getNext();
            before.setNext(after);
            after.setPrev(before);
            node.setNext(null);
            node.setPrev(null);
            length--;
        }

        protected void concat(HeapNodeList list) {
            HeapNode thisFirst = this.getFirst();
            HeapNode thisLast = this.getLast();
            HeapNode listFirst = list.getFirst();
            HeapNode listLast = list.getLast();

            thisLast.setNext(listFirst); // Instead of this.sentinel
            listFirst.setPrev(thisLast); // Instead of list.sentinel
            sentinel.setPrev(listLast); // Instead of thisLast
            listLast.setNext(sentinel); // Instead of list.sentinel
            this.length += list.getLength();

            // We don't want list to be used later.
            list.sentinel.setNext(null);
            list.sentinel.setPrev(null);
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
    }
}
