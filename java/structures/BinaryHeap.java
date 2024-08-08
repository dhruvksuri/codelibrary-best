package structures;

import java.util.*;

// https://en.wikipedia.org/wiki/Binary_heap

// invariant: heap[parent] <= heap[child]
public class BinaryHeap {
    int[] heap;  // 0 index based
    int size;

    public BinaryHeap(int n) {
        heap = new int[n];
    }

    // build heap in O(n)
    public BinaryHeap(int[] values) {
        heap = values.clone();
        size = values.length;
        // due to 0 index based => pos = size / 2 - 1
        for (int pos = size / 2 - 1; pos >= 0; pos--) down(pos);
    }

    public int removeMin() {
        int removed = heap[0];
        heap[0] = heap[--size];
        down(0);
        return removed;
    }

    public void add(int value) {
        heap[size] = value;
        up(size++);
    }

    void up(int child) {
        while (child > 0) {
            int parent = (child - 1) / 2;
            if (heap[parent] <= heap[child])
                break;
            swap(child, parent);
            child = parent;
        }
    }

    void down(int pos) {
        while (true) {
            int child = 2 * pos + 1;
            if (child >= size)
                break;
            
            // Go to the smallest of both childs
            if (child + 1 < size && heap[child + 1] < heap[child])
                ++child;
            if (heap[pos] <= heap[child])
                break;
            swap(pos, child);
            pos = child;
        }
    }

    void swap(int i, int j) {
        int t = heap[i];
        heap[i] = heap[j];
        heap[j] = t;
    }

    // random test
    public static void main(String[] args) {
        Random rnd = new Random(1);
        for (int step = 0; step < 1000; step++) {
            int n = rnd.nextInt(100) + 1;
            PriorityQueue<Integer> q = new PriorityQueue<>();
            BinaryHeap h = new BinaryHeap(n);
            for (int op = 0; op < 1000; op++) {
                if (rnd.nextBoolean() && q.size() < n) {
                    int v = rnd.nextInt();
                    q.add(v);
                    h.add(v);
                } else if (!q.isEmpty()) {
                    if (q.remove() != h.removeMin())
                        throw new RuntimeException();
                }
            }
        }
    }
}
