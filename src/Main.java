import java.util.*;

public class Main {

    public static void main(String[] args) {
        FibonacciHeap heap = new FibonacciHeap();
        int[] insertByOrder = new int[]{7, 6, 5, 8, 4, 3, 9, 2, 1, 1, 19, 17, 15, 13, 12, 11, 21};
        for (int i = 0; i < insertByOrder.length; i++) {
            FibonacciHeap.HeapNode min = heap.findMin();
            if (min == null)
                System.out.println("Empty Heap");
            else
                System.out.println("Min=" + heap.findMin().getKey());
            System.out.println("Inserting " + insertByOrder[i]);
            heap.insert(insertByOrder[i]);
        }
        heap.deleteMin();
        int[] list = heap.kMin(heap, 10);
        System.out.println(Arrays.toString(list));
    }
}
