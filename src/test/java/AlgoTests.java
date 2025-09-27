import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Random;

import org.example.ClosestPair.Point;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

import org.example.MergeSort;
import org.example.QuickSort;
import org.example.DeterministicSelect;
import org.example.ClosestPair;
import org.example.ClosestPair.Point; // Не забудьте импорт для вложенного класса Point


class AlgoTests {

    @Test
    void testMergeSortCorrectness() {
        int[] arr = {5, 2, 9, 1, 5, 6};
        int[] expected = arr.clone();
        Arrays.sort(expected);

        MergeSort.Metrics m = new MergeSort.Metrics();
        int[] buf = new int[arr.length];
        MergeSort.mergeSort(arr, buf, 0, arr.length - 1, m, 1);

        assertArrayEquals(expected, arr);
    }

    @Test
    void testQuickSortCorrectness() {
        int[] arr = {3, 7, 8, 5, 2, 1, 9, 5, 4};
        int[] expected = arr.clone();
        Arrays.sort(expected);

        QuickSort.Metrics m = new QuickSort.Metrics();
        QuickSort.quickSort(arr, 0, arr.length - 1, m, 1);

        assertArrayEquals(expected, arr);
    }

    @Test
    void testQuickSortRecursionDepth() {
        int n = 100_000;
        int[] arr = new Random().ints(n, 0, n).toArray();

        QuickSort.Metrics m = new QuickSort.Metrics();
        QuickSort.quickSort(arr, 0, arr.length - 1, m, 1);
        int depth = m.maxDepth;

        assertTrue(depth <= 2 * (Math.log(n) / Math.log(2)) + 50);
    }

    @Test
    void testDeterministicSelect() {
        int[] arr = new Random().ints(1000, 0, 10000).toArray();
        int k = 500;

        int[] copy = arr.clone();
        Arrays.sort(copy);
        int expected = copy[k];

        DeterministicSelect.Metrics m = new DeterministicSelect.Metrics();
        int actual = DeterministicSelect.select(arr, k, m);

        assertEquals(expected, actual);
    }

    @Test
    void testClosestPairSmall() {
        Point[] points = {
                new Point(0, 0),
                new Point(3, 4), // dist = 5
                new Point(7, 7),
                new Point(1, 1)  // dist = sqrt(2)
        };

        ClosestPair.Metrics m = new ClosestPair.Metrics();
        double dist = ClosestPair.closestPair(points, m);

        assertEquals(Math.sqrt(2), dist, 1e-9);
    }
}