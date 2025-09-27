package org.example;

import java.util.Random;

public class QuickSort {

    private static final Random rand = new Random();

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

    public static void main(String[] args) {
        int[] numbers = new int[20];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = rand.nextInt(1000);
        }

        System.out.println("Before:");
        printArray(numbers);

        Metrics m = new Metrics();

        long start = System.nanoTime();
        quickSort(numbers, 0, numbers.length - 1, m, 1);
        long end = System.nanoTime();

        System.out.println("\nAfter:");
        printArray(numbers);

        System.out.println("\nMetrics:");
        System.out.println(m);
        System.out.printf("Time = %.3f ms%n", (end - start) / 1_000_000.0);
    }

    public static void quickSort(int[] arr, int lo, int hi, Metrics m, int depth) {
        while (lo < hi) {
            m.calls++;
            if (depth > m.maxDepth) m.maxDepth = depth;

            int pivotIndex = lo + rand.nextInt(hi - lo + 1);
            swap(arr, pivotIndex, hi, m);

            int p = partition(arr, lo, hi, arr[hi], m);

            if (p - lo < hi - p) {
                quickSort(arr, lo, p - 1, m, depth + 1);
                lo = p + 1;
            } else {
                quickSort(arr, p + 1, hi, m, depth + 1);
                hi = p - 1;
            }
        }
    }

    private static int partition(int[] arr, int lo, int hi, int pivot, Metrics m) {
        int i = lo;
        for (int j = lo; j < hi; j++) {
            m.comparisons++;
            if (arr[j] <= pivot) {
                swap(arr, i, j, m);
                i++;
            }
        }
        swap(arr, i, hi, m);
        return i;
    }

    private static void swap(int[] arr, int i, int j, Metrics m) {
        if (i != j) {
            int tmp = arr[i];
            arr[i] = arr[j];
            arr[j] = tmp;
            m.swaps++;
        }
    }

    private static void printArray(int[] arr) {
        for (int x : arr) {
            System.out.print(x + " ");
        }
        System.out.println();
    }
}
