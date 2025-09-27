package org.example;

import java.util.*;

public class ClosestPair {

    public static class Point {
        int x, y;
        public Point(int x, int y) { this.x = x; this.y = y; }
        @Override
        public String toString() {
            return "(" + x + "," + y + ")";
        }
    }

    public static class Metrics {
        public long comparisons = 0;
        public int calls = 0;
        public int maxDepth = 0;
    }

    public static double closestPair(Point[] points, Metrics m) {
        Arrays.sort(points, Comparator.comparingInt(p -> p.x));
        return closest(points, 0, points.length - 1, m, 1);
    }

    private static double closest(Point[] pts, int left, int right, Metrics m, int depth) {
        m.calls++;
        if (depth > m.maxDepth) m.maxDepth = depth;

        if (right - left <= 3) {
            return bruteForce(pts, left, right, m);
        }

        int mid = (left + right) / 2;
        double d1 = closest(pts, left, mid, m, depth + 1);
        double d2 = closest(pts, mid + 1, right, m, depth + 1);
        double d = Math.min(d1, d2);

        List<Point> strip = new ArrayList<>();
        int midX = pts[mid].x;
        for (int i = left; i <= right; i++) {
            if (Math.abs(pts[i].x - midX) < d) {
                strip.add(pts[i]);
            }
        }

        strip.sort(Comparator.comparingInt(p -> p.y));

        for (int i = 0; i < strip.size(); i++) {
            for (int j = i + 1; j < strip.size() && (strip.get(j).y - strip.get(i).y) < d; j++) {
                d = Math.min(d, dist(strip.get(i), strip.get(j), m));
            }
        }
        return d;
    }

    private static double bruteForce(Point[] pts, int left, int right, Metrics m) {
        double min = Double.POSITIVE_INFINITY;
        for (int i = left; i <= right; i++) {
            for (int j = i + 1; j <= right; j++) {
                min = Math.min(min, dist(pts[i], pts[j], m));
            }
        }
        return min;
    }

    private static double dist(Point a, Point b, Metrics m) {
        m.comparisons++;
        int dx = a.x - b.x, dy = a.y - b.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static void main(String[] args) {
        Point[] pts = {
                new Point(2, 3), new Point(12, 30), new Point(40, 50),
                new Point(5, 1), new Point(12, 10), new Point(3, 4)
        };

        Metrics m = new Metrics();

        long start = System.nanoTime();
        double d = closestPair(pts, m);
        long end = System.nanoTime();

        System.out.println("Points: " + Arrays.toString(pts));
        System.out.println("Минимальная дистанция = " + d);

        System.out.println("\nMetrics:");
        System.out.println("Comparisons = " + m.comparisons);
        System.out.println("Calls = " + m.calls);
        System.out.println("Max depth = " + m.maxDepth);
        System.out.printf("Time = %.3f ms%n", (end - start) / 1_000_000.0);
    }
}
