package org.hildan.minecraft.mining.optimizer.patterns.generated;

import org.hildan.minecraft.mining.optimizer.chunks.Sample;
import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern;

import java.util.Iterator;

/**
 * Generates digging patterns.
 */
public class PatternGenerator implements Iterable<DiggingPattern> {

    private final Sample base;

    private final GenerationConstraints constraints;

    public PatternGenerator(Sample base, GenerationConstraints constraints) {
        this.base = base;
        this.constraints = constraints;
    }

    @Override
    public Iterator<DiggingPattern> iterator() {
        // FIXME generate accesses?
        return new PatternIterator(base, null, constraints);
    }
}
