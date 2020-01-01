import java.util.*;

public class Measurements {
    public static void main(String[] args) {
        /*for(int i = 10; i <= 20; i++){
            runFirstTest(i , 50);
        }*/
        for (int i = 1; i <= 20; i++) {
            runSecondTest(1000 * i);
        }
    }

    public static void runFirstTest(int exponent, int delta) {
        int m = (int) Math.pow(2, exponent);
        FibonacciHeap heap = new FibonacciHeap();
        HashMap<Integer, FibonacciHeap.HeapNode> nodes = insertRange(heap, m, 0);
        FibonacciHeap.HeapNode theNodeToBeDeleted = heap.findMin();
        int k = exponent - 2;
        long start = System.currentTimeMillis();
        int linksBefore = FibonacciHeap.totalLinks();
        int cutsBefore = FibonacciHeap.totalCuts();
        heap.deleteMin();
        System.out.println("m=2^" + exponent + "\ttest started at: " + start);
        for (int i = 1; i <= exponent - 2; i++) {
            int key = (int) (m * (1 - 2 * Math.pow(0.5, i + 1)) + 2);
            FibonacciHeap.HeapNode node = null;
            if (nodes.containsKey(key)) {
                node = nodes.get((int) key);
                System.out.println("\tDecresing key " + key + " to " + (key - delta));
            } else
                System.out.println("\tKey " + key + " does not exist.");
            if (node != null)
                heap.decreaseKey(node, delta);
        }
        System.out.println("\tDecresing key " + (m - 1) + " to " + (m - 1 - delta));
        heap.decreaseKey(nodes.get(m - 1), delta);
        long end = System.currentTimeMillis();
        int linksAfter = FibonacciHeap.totalLinks();
        int cutsAfter = FibonacciHeap.totalCuts();
        System.out.println("test ended at: " + end);
        System.out.println("it took " + (end - start) + " ms.");
        System.out.println("totalLinks: " + (linksAfter - linksBefore));
        System.out.println("totalCuts: " + (cutsAfter - cutsBefore));
        System.out.println("Potential: " + heap.potential());
    }

    public static void runSecondTest(int m) {
        FibonacciHeap heap = new FibonacciHeap();
        insertRange(heap, m, 0);
        int linksBefore = FibonacciHeap.totalLinks();
        int cutsBefore = FibonacciHeap.totalCuts();
        //System.out.println("m="+ m + "\ttest started at: " + start);
        long start = System.currentTimeMillis();
        for (int i = 0; i <= m * 99 / 100; i++) {
            //FibonacciHeap.HeapNode min = heap.findMin();
            //System.out.println("Deleting key " + min.getKey());
            heap.deleteMin();
        }
        long end = System.currentTimeMillis();
        int linksAfter = FibonacciHeap.totalLinks();
        int cutsAfter = FibonacciHeap.totalCuts();
        System.out.println("test ended at: " + end);
        System.out.println("it took " + (end - start) + " ms.");
        System.out.println("totalLinks: " + (linksAfter - linksBefore));
        System.out.println("totalCuts: " + (cutsAfter - cutsBefore));
        System.out.println("Potential: " + heap.potential());
    }

    public static HashMap<Integer, FibonacciHeap.HeapNode> addKeys(FibonacciHeap heap, Collection<Integer> keys) {
        HashMap<Integer, FibonacciHeap.HeapNode> nodes = new HashMap<>();
        for (Integer key : keys)
            nodes.put(key, heap.insert(key));
        return nodes;
    }

    public static void deleteNKeys(FibonacciHeap heap, int n) {
        for (int i = 1; i <= n; i++)
            heap.deleteMin();
    }

    public static void emptyHeap(FibonacciHeap heap) {
        deleteNKeys(heap, heap.size());
    }

    public static HashMap<Integer, FibonacciHeap.HeapNode> insertRange(FibonacciHeap heap, int first, int last) {
        int delta = 1;
        HashMap<Integer, FibonacciHeap.HeapNode> nodes = new HashMap<>();
        if (first > last)
            delta = -1;
        for (int i = first; (first <= i && i <= last) || (last <= i && i <= first); i += delta) {
            nodes.put(i, heap.insert(i));
        }
        return nodes;
    }
}
