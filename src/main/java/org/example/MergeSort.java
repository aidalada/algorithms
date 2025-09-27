package org.example;

import java.util.Arrays;
import java.util.Random;

public class MergeSort {

    private static final int INSERTION_SORT_CUTOFF = 16;

    public static class Metrics {
        public long comparisons = 0;
        public long copies = 0;
        public int calls = 0;
        public int maxDepth = 0;

        public void reset() {
            comparisons = copies = 0;
            calls = maxDepth = 0;
        }

        @Override
        public String toString() {
            return "Comparisons = " + comparisons +
                    ", Copies = " + copies +
                    ", Calls = " + calls +
                    ", Max depth = " + maxDepth;
        }
    }

    public static void main(String[] args) {
        Random rand = new Random();
        int[] numbers = new int[20];

        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = rand.nextInt(1000000);
        }

        System.out.println("Before:");
        System.out.println(Arrays.toString(numbers));

        Metrics m = new Metrics();
        int[] buf = new int[numbers.length];

        long start = System.nanoTime();
        mergeSort(numbers, buf, 0, numbers.length - 1, m, 1);
        long end = System.nanoTime();

        System.out.println("\nAfter:");
        System.out.println(Arrays.toString(numbers));

        System.out.println("\nMetrics:");
        System.out.println(m);
        System.out.printf("Time = %.3f ms%n", (end - start) / 1_000_000.0);
    }

    public static void mergeSort(int[] a, int[] buf, int lo, int hi, Metrics m, int depth) {
        m.calls++;
        if (depth > m.maxDepth) m.maxDepth = depth;

        if (hi - lo + 1 <= INSERTION_SORT_CUTOFF) {
            insertionSort(a, lo, hi, m);
            return;
        }

        int mid = lo + (hi - lo) / 2;
        mergeSort(a, buf, lo, mid, m, depth + 1);
        mergeSort(a, buf, mid + 1, hi, m, depth + 1);

        merge(a, buf, lo, mid, hi, m);
    }

    private static void insertionSort(int[] a, int lo, int hi, Metrics m) {
        for (int i = lo + 1; i <= hi; i++) {
            int key = a[i];
            int j = i - 1;
            while (j >= lo && compare(a[j], key, m) > 0) {
                a[j + 1] = a[j];
                m.copies++;
                j--;
            }
            a[j + 1] = key;
            m.copies++;
        }
    }

    private static void merge(int[] a, int[] buf, int lo, int mid, int hi, Metrics m) {
        int i = lo, j = mid + 1, k = lo;

        for (int p = lo; p <= hi; p++) {
            buf[p] = a[p];
            m.copies++;
        }

        while (i <= mid && j <= hi) {
            if (compare(buf[i], buf[j], m) <= 0) {
                a[k++] = buf[i++];
            } else {
                a[k++] = buf[j++];
            }
            m.copies++;
        }

        while (i <= mid) {
            a[k++] = buf[i++];
            m.copies++;
        }
        while (j <= hi) {
            a[k++] = buf[j++];
            m.copies++;
        }
    }

    private static int compare(int x, int y, Metrics m) {
        m.comparisons++;
        return Integer.compare(x, y);
    }
}
