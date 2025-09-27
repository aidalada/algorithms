package org.example;

import java.util.Arrays;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        Random rand = new Random();

        int[] arr1 = rand.ints(20, 0, 1000).toArray();
        int[] copy1 = Arrays.copyOf(arr1, arr1.length);

        System.out.println("==== MergeSort ====");
        System.out.println("Before: " + Arrays.toString(arr1));

        MergeSort.Metrics m1 = new MergeSort.Metrics();
        int[] buf = new int[arr1.length];
        long t1 = System.nanoTime();
        MergeSort.mergeSort(arr1, buf, 0, arr1.length - 1, m1, 1);
        long t2 = System.nanoTime();

        System.out.println("After: " + Arrays.toString(arr1));
        printMetrics(m1, t2 - t1);
        System.out.println();

        int[] arr2 = Arrays.copyOf(copy1, copy1.length);

        System.out.println("==== QuickSort ====");
        System.out.println("Before: " + Arrays.toString(arr2));

        QuickSort.Metrics m2 = new QuickSort.Metrics();
        long t3 = System.nanoTime();
        QuickSort.quickSort(arr2, 0, arr2.length - 1, m2, 1);
        long t4 = System.nanoTime();

        System.out.println("After: " + Arrays.toString(arr2));
        printMetrics(m2, t4 - t3);
        System.out.println();

        int[] arr3 = Arrays.copyOf(copy1, copy1.length);
        int k = arr3.length / 2; // медиана
        DeterministicSelect.Metrics m3 = new DeterministicSelect.Metrics();

        System.out.println("==== Deterministic Select ====");
        System.out.println("Array: " + Arrays.toString(arr3));

        long t5 = System.nanoTime();
        int kth = DeterministicSelect.select(arr3, k, m3); // <-- 3 аргумента
        long t6 = System.nanoTime();

        System.out.println(k + "-й элемент (медиана) = " + kth);

        System.out.println("\nMetrics:");
        System.out.println(m3);
        System.out.printf("Time = %.3f ms%n", (t6 - t5) / 1_000_000.0);


        ClosestPair.Point[] pts = {
                new ClosestPair.Point(2, 3), new ClosestPair.Point(12, 30),
                new ClosestPair.Point(40, 50), new ClosestPair.Point(5, 1),
                new ClosestPair.Point(12, 10), new ClosestPair.Point(3, 4)
        };

        System.out.println("==== Closest Pair ====");
        System.out.println("Points: " + Arrays.toString(pts));

        ClosestPair.Metrics m4 = new ClosestPair.Metrics();
        long t7 = System.nanoTime();
        double d = ClosestPair.closestPair(pts, m4);
        long t8 = System.nanoTime();

        System.out.println("Минимальная дистанция = " + d);
        printMetrics(m4, t8 - t7);
    }

    private static void printMetrics(Object m, long nanos) {
        if (m instanceof MergeSort.Metrics mm) {
            System.out.println("Comparisons = " + mm.comparisons);
            System.out.println("Copies = " + mm.copies);
            System.out.println("Calls = " + mm.calls);
            System.out.println("Max depth = " + mm.maxDepth);
        } else if (m instanceof QuickSort.Metrics qm) {
            System.out.println("Comparisons = " + qm.comparisons);
            System.out.println("Swaps = " + qm.swaps);
            System.out.println("Calls = " + qm.calls);
            System.out.println("Max depth = " + qm.maxDepth);
        } else if (m instanceof ClosestPair.Metrics cm) {
            System.out.println("Comparisons = " + cm.comparisons);
            System.out.println("Calls = " + cm.calls);
            System.out.println("Max depth = " + cm.maxDepth);
        }
        System.out.printf("Time = %.3f ms%n", nanos / 1_000_000.0);
    }
}
