import java.util.*;

public class Main {

    public static void main(String[] args) {
        ArrayList<Integer> insertByOrder = new ArrayList<>(Arrays.asList(2, 3, 5, 0, 7, 8, 6, 1, 4));
        FibonacciHeap heap = new FibonacciHeap();
        for (Integer key : insertByOrder)
            heap.insert(key);
        System.out.println("Tree Size\tStart\tEnd\tEnd-Start\tValid");
        for (int k = 0; k <= 25; k++) {
            int size = (int) Math.pow(2, k);
            runTest1(size, size / 2);
        }
    }

    public static void runTest1(int size, int k) {
        FibonacciHeap heap = new FibonacciHeap();
        ArrayList<Integer> insertByOrder = new ArrayList<>();
        for (int i = 0; i <= size; i++)
            insertByOrder.add(i);
        Collections.shuffle(insertByOrder);
        for (int i = 0; i < insertByOrder.size(); i++) {
            heap.insert(insertByOrder.get(i));
        }
        boolean valid = true;
        if (heap.findMin().getKey() != 0)
            valid = false;
        heap.deleteMin();
        if (heap.findMin() != null && heap.findMin().getKey() != 1)
            valid = false;
        long start = System.currentTimeMillis();
        int[] list = heap.kMin(heap, k);
        long end = System.currentTimeMillis();
        Collections.sort(insertByOrder);
        insertByOrder.remove(0);

        for (int i = 0; i < k; i++)
            if (insertByOrder.get(i) != list[i])
                valid = false;
        System.out.println(size + "\t" + start + "\t" + end + "\t" + (end - start) + "\t" + valid);
    }
}
