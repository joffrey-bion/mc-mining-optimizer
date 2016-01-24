package org.hildan.minecraft.mining.optimizer.statistics;

import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern;

import java.util.ArrayList;

/**
 * Stores patterns and their stats, keeping only the best ones.
 */
public class PatternStore {

    private static class EvaluatedPattern {
        DiggingPattern pattern;
        Statistics statistics;

        EvaluatedPattern(DiggingPattern pattern, Statistics statistics) {
            this.pattern = pattern;
            this.statistics = statistics;
        }
    }

    private final double margin;

    private final ArrayList<EvaluatedPattern> patterns = new ArrayList<>(20);

    public PatternStore(double margin) {
        this.margin = margin;
    }

    public boolean add(DiggingPattern pattern, Statistics stats) {
        if (patterns.stream().anyMatch(p -> p.statistics.isBetterThan(stats, margin))) {
            // new pattern not worth adding
            return false;
        }
        // remove inferior patterns
        patterns.removeIf(p -> stats.isBetterThan(p.statistics, margin));
        patterns.add(new EvaluatedPattern(pattern, stats));
        return true;
    }

}
