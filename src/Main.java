import java.util.*;

public class Main {

    public static void main(String[] args) {
        FibonacciHeap heap = new FibonacciHeap();
        int[] insertByOrder = new int[]{7, 6, 5, 8, 4, 3, 9, 2, 1, 1};
        for (int i = 0; i < insertByOrder.length; i++) {
            FibonacciHeap.HeapNode min = heap.findMin();
            if (min == null)
                System.out.println("Empty Heap");
            else
                System.out.println("Min=" + heap.findMin().getKey());
            System.out.println("Inseting " + insertByOrder[i]);
            heap.insert(insertByOrder[i]);
        }
        for (int i = insertByOrder.length; i >= 0; i--) {
            FibonacciHeap.HeapNode min = heap.findMin();
            if (min != null)
                System.out.println("Deleting minimum: " + min.getKey());
            heap.deleteMin();
        }
    }
}
