package org.hildan.minecraft.mining.optimizer.statistics;

import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Stores patterns and their stats, keeping only the best ones.
 */
public class PatternStore implements Iterable<EvaluatedPattern> {

    private final double margin;

    private final Collection<EvaluatedPattern> patterns = new ArrayList<>(20);

    public PatternStore(double margin) {
        this.margin = margin;
    }

    public boolean add(DiggingPattern pattern, Statistics stats) {
        if (patterns.stream().anyMatch(p -> p.getStatistics().isBetterThan(stats, margin))) {
            // new pattern not worth adding
            return false;
        }
        // remove inferior patterns
        patterns.removeIf(p -> stats.isBetterThan(p.getStatistics(), margin));
        patterns.add(new EvaluatedPattern(pattern, stats));
        return true;
    }

    @Override
    public Iterator<EvaluatedPattern> iterator() {
        return patterns.iterator();
    }

    @Override
    public String toString() {
        return String.format("%d patterns: %s", patterns.size(), patterns);
    }
}
