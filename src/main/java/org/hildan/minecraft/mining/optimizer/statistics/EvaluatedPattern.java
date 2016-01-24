package org.hildan.minecraft.mining.optimizer.statistics;

import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern;

/**
 * A pattern along with its statistics.
 */
public class EvaluatedPattern {
    private final DiggingPattern pattern;

    private final Statistics statistics;

    EvaluatedPattern(DiggingPattern pattern, Statistics statistics) {
        this.pattern = pattern;
        this.statistics = statistics;
    }

    public DiggingPattern getPattern() {
        return pattern;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    @Override
    public String toString() {
        return statistics.toString();
    }
}
