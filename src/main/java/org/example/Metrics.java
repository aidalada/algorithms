package org.example;

public class Metrics {
    public long comparisons = 0;
    public long copies = 0;
    public int maxDepth = 0;
    private int currentDepth = 0;

    public void incComparisons() {
        comparisons++;
    }

    public void incCopies() {
        copies++;
    }

    public void enterRecursion() {
        currentDepth++;
        if (currentDepth > maxDepth) {
            maxDepth = currentDepth;
        }
    }

    public void exitRecursion() {
        currentDepth--;
    }

    public void reset() {
        comparisons = 0;
        copies = 0;
        maxDepth = 0;
        currentDepth = 0;
    }

    @Override
    public String toString() {
        return "comparisons=" + comparisons +
                ", copies=" + copies +
                ", maxDepth=" + maxDepth;
    }
}
