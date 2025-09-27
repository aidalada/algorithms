package org.example;

import java.util.Arrays;

public class DeterministicSelect {

    public static class Metrics {
        public long comparisons = 0;
        public long swaps = 0;
        public int calls = 0;
        public int maxDepth = 0;

        public void reset() {
            comparisons = swaps = 0;
            calls = maxDepth = 0;
        }

        @Override
        public String toString() {
            return "Comparisons = " + comparisons +
                    ", Swaps = " + swaps +
                    ", Calls = " + calls +
                    ", Max depth = " + maxDepth;
        }
    }

    public static int select(int[] arr, int k, Metrics m) {
        if (k < 0 || k >= arr.length) throw new IllegalArgumentException("k out of range");
        return select(arr, 0, arr.length - 1, k, m, 1);
    }

    private static int select(int[] arr, int left, int right, int k, Metrics m, int depth) {
        m.calls++;
        if (depth > m.maxDepth) m.maxDepth = depth;

        if (left == right) return arr[left];

        int pivot = medianOfMedians(arr, left, right, m);
        int pivotIndex = partition(arr, left, right, pivot, m); // теперь pivotIndex — позиция одного pivot

        if (k == pivotIndex) {
            return arr[k];
        } else if (k < pivotIndex) {
            return select(arr, left, pivotIndex - 1, k, m, depth + 1);
        } else {
            return select(arr, pivotIndex + 1, right, k, m, depth + 1);
        }
    }

    private static int partition(int[] arr, int left, int right, int pivotValue, Metrics m) {
        int pivotPos = left;
        while (pivotPos <= right && arr[pivotPos] != pivotValue) pivotPos++;
        if (pivotPos > right) {
            pivotPos = right;
        }
        swap(arr, pivotPos, right, m);

        int store = left;
        for (int i = left; i < right; i++) {
            m.comparisons++;
            if (arr[i] < arr[right]) {
                swap(arr, store, i, m);
                store++;
            }
        }
        swap(arr, store, right, m);
        return store;
    }

    private static int medianOfMedians(int[] arr, int left, int right, Metrics m) {
        int n = right - left + 1;
        if (n <= 5) {
            Arrays.sort(arr, left, right + 1);
            return arr[left + n / 2];
        }

        int numMedians = (n + 4) / 5; // ceil(n/5)
        int[] medians = new int[numMedians];

        for (int i = 0; i < numMedians; i++) {
            int subLeft = left + i * 5;
            int subRight = Math.min(subLeft + 4, right);
            Arrays.sort(arr, subLeft, subRight + 1);
            medians[i] = arr[subLeft + (subRight - subLeft) / 2];
        }

        return medianOfMedians(medians, 0, numMedians - 1, m);
    }

    private static void swap(int[] arr, int i, int j, Metrics m) {
        if (i != j) {
            int tmp = arr[i];
            arr[i] = arr[j];
            arr[j] = tmp;
            if (m != null) m.swaps++;
        }
    }

    public static void main(String[] args) {
        int[] a = {12, 3, 5, 7, 4, 19, 26};
        int k = 3; // 0-based

        Metrics m = new Metrics();
        long start = System.nanoTime();
        int kth = select(a, k, m);
        long end = System.nanoTime();

        System.out.println("Array (possibly mutated): " + Arrays.toString(a));
        System.out.println(k + "-й элемент (0-based) = " + kth);

        System.out.println("\nMetrics:");
        System.out.println(m);
        System.out.printf("Time = %.3f ms%n", (end - start) / 1_000_000.0);
    }
}
