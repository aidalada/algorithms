package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class Benchmark {

    private static final int[] SIZES = {100, 500, 1000, 2000, 5000, 10000};
    private static final int TRIALS = 5;
    private static final Random RAND = new Random(123456789);
    private static final double EPS = 1e-9;

    public static void main(String[] args) throws Exception {
        runAllAndWriteCsv("results.csv");
        System.out.println("Benchmark finished. See results.csv");
    }

    public static void runAllAndWriteCsv(String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("algorithm,n,avg_time_ms,avg_comparisons,avg_swaps_or_copies,avg_calls,avg_maxDepth,all_correct\n");

            for (int n : SIZES) {
                System.out.println("Running n = " + n);
                // MergeSort
                BenchmarkResult mergeRes = benchMergeSort(n);
                writer.write(formatCsvLine("MergeSort", n, mergeRes));
                // QuickSort
                BenchmarkResult quickRes = benchQuickSort(n);
                writer.write(formatCsvLine("QuickSort", n, quickRes));
                // Deterministic Select (median k = n/2)
                BenchmarkResult selectRes = benchSelect(n);
                writer.write(formatCsvLine("DeterministicSelect", n, selectRes));
                // Closest Pair
                BenchmarkResult closestRes = benchClosestPair(n);
                writer.write(formatCsvLine("ClosestPair", n, closestRes));
            }
        }
    }


    private static BenchmarkResult benchMergeSort(int n) {
        double totalTimeNs = 0;
        long totalComparisons = 0;
        long totalCopies = 0;
        long totalCalls = 0;
        long totalMaxDepth = 0;
        boolean allCorrect = true;

        for (int t = 0; t < TRIALS; t++) {
            int[] arr = generateArray(n);
            int[] expected = arr.clone();
            Arrays.sort(expected);

            int[] a = arr.clone();
            MergeSort.Metrics m = new MergeSort.Metrics();
            int[] buf = new int[a.length];

            long s = System.nanoTime();
            MergeSort.mergeSort(a, buf, 0, a.length - 1, m, 1);
            long e = System.nanoTime();

            totalTimeNs += (e - s);
            totalComparisons += m.comparisons;
            totalCopies += m.copies;
            totalCalls += m.calls;
            totalMaxDepth += m.maxDepth;

            if (!Arrays.equals(a, expected)) allCorrect = false;
        }

        return averageResult(totalTimeNs, totalComparisons, totalCopies, totalCalls, totalMaxDepth, allCorrect);
    }

    private static BenchmarkResult benchQuickSort(int n) {
        double totalTimeNs = 0;
        long totalComparisons = 0;
        long totalSwaps = 0;
        long totalCalls = 0;
        long totalMaxDepth = 0;
        boolean allCorrect = true;

        for (int t = 0; t < TRIALS; t++) {
            int[] arr = generateArray(n);
            int[] expected = arr.clone();
            Arrays.sort(expected);

            int[] a = arr.clone();
            QuickSort.Metrics m = new QuickSort.Metrics();

            long s = System.nanoTime();
            QuickSort.quickSort(a, 0, a.length - 1, m, 1);
            long e = System.nanoTime();

            totalTimeNs += (e - s);
            totalComparisons += m.comparisons;
            totalSwaps += m.swaps;
            totalCalls += m.calls;
            totalMaxDepth += m.maxDepth;

            if (!Arrays.equals(a, expected)) allCorrect = false;
        }

        return averageResult(totalTimeNs, totalComparisons, totalSwaps, totalCalls, totalMaxDepth, allCorrect);
    }

    private static BenchmarkResult benchSelect(int n) {
        double totalTimeNs = 0;
        long totalComparisons = 0;
        long totalSwaps = 0;
        long totalCalls = 0;
        long totalMaxDepth = 0;
        boolean allCorrect = true;

        for (int t = 0; t < TRIALS; t++) {
            int[] arr = generateArray(n);
            int[] expectedSorted = arr.clone();
            Arrays.sort(expectedSorted);
            int k = n / 2;

            int[] a = arr.clone(); // select mutates array
            DeterministicSelect.Metrics m = new DeterministicSelect.Metrics();

            long s = System.nanoTime();
            int val = DeterministicSelect.select(a, k, m);
            long e = System.nanoTime();

            totalTimeNs += (e - s);
            totalComparisons += m.comparisons;
            totalSwaps += m.swaps;
            totalCalls += m.calls;
            totalMaxDepth += m.maxDepth;

            if (val != expectedSorted[k]) allCorrect = false;
        }

        return averageResult(totalTimeNs, totalComparisons, totalSwaps, totalCalls, totalMaxDepth, allCorrect);
    }

    private static BenchmarkResult benchClosestPair(int n) {
        double totalTimeNs = 0;
        long totalComparisons = 0;
        long totalCalls = 0;
        long totalMaxDepth = 0;
        boolean allCorrect = true;

        for (int t = 0; t < TRIALS; t++) {
            ClosestPair.Point[] pts = generatePoints(n);

            ClosestPair.Point[] ptsForBrute = deepCopyPoints(pts);
            double expected = bruteForceClosest(ptsForBrute);

            ClosestPair.Point[] ptsForAlg = deepCopyPoints(pts);
            ClosestPair.Metrics m = new ClosestPair.Metrics();

            long s = System.nanoTime();
            double ans = ClosestPair.closestPair(ptsForAlg, m);
            long e = System.nanoTime();

            totalTimeNs += (e - s);
            totalComparisons += m.comparisons;
            totalCalls += m.calls;
            totalMaxDepth += m.maxDepth;

            if (Math.abs(ans - expected) > 1e-6) allCorrect = false;
        }

        return averageResult(totalTimeNs, totalComparisons, /* swapsOrCopies */ 0, totalCalls, totalMaxDepth, allCorrect);
    }


    private static int[] generateArray(int n) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = RAND.nextInt(1_000_000);
        return a;
    }

    private static ClosestPair.Point[] generatePoints(int n) {
        ClosestPair.Point[] pts = new ClosestPair.Point[n];
        for (int i = 0; i < n; i++) {
            int x = RAND.nextInt(1_000_000);
            int y = RAND.nextInt(1_000_000);
            pts[i] = new ClosestPair.Point(x, y);
        }
        return pts;
    }

    private static ClosestPair.Point[] deepCopyPoints(ClosestPair.Point[] src) {
        ClosestPair.Point[] dst = new ClosestPair.Point[src.length];
        for (int i = 0; i < src.length; i++) dst[i] = new ClosestPair.Point(src[i].x, src[i].y);
        return dst;
    }

    private static double bruteForceClosest(ClosestPair.Point[] pts) {
        double best = Double.POSITIVE_INFINITY;
        for (int i = 0; i < pts.length; i++) {
            for (int j = i + 1; j < pts.length; j++) {
                double d = Math.hypot(pts[i].x - pts[j].x, pts[i].y - pts[j].y);
                if (d < best) best = d;
            }
        }
        return best;
    }

    private static BenchmarkResult averageResult(double totalTimeNs, long totalComparisons,
                                                 long totalSwapsOrCopies, long totalCalls,
                                                 long totalMaxDepth, boolean allCorrect) {
        BenchmarkResult r = new BenchmarkResult();
        r.avgTimeMs = (totalTimeNs / TRIALS) / 1_000_000.0;
        r.avgComparisons = totalComparisons / (double) TRIALS;
        r.avgSwapsOrCopies = totalSwapsOrCopies / (double) TRIALS;
        r.avgCalls = totalCalls / (double) TRIALS;
        r.avgMaxDepth = totalMaxDepth / (double) TRIALS;
        r.allCorrect = allCorrect;
        return r;
    }

    private static String formatCsvLine(String algo, int n, BenchmarkResult r) {
        return String.format("%s,%d,%.6f,%.2f,%.2f,%.2f,%.2f,%b\n",
                algo, n, r.avgTimeMs, r.avgComparisons, r.avgSwapsOrCopies, r.avgCalls, r.avgMaxDepth, r.allCorrect);
    }

    private static class BenchmarkResult {
        double avgTimeMs;
        double avgComparisons;
        double avgSwapsOrCopies;
        double avgCalls;
        double avgMaxDepth;
        boolean allCorrect;
    }
}
