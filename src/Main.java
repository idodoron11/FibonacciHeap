import java.util.Iterator;

public class Main {

    public static void main(String[] args) {
        FibonacciHeap heap = new FibonacciHeap();
        heap.insert(1);
        heap.insert(2);
        heap.insert(3);
        heap.insert(4);
        System.out.println(heap.findMin().getKey());
    }
}
