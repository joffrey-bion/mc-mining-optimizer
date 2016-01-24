package org.hildan.minecraft.mining.optimizer.patterns.generated;

import org.hildan.minecraft.mining.optimizer.chunks.Sample;
import org.hildan.minecraft.mining.optimizer.patterns.Access;
import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern;

import java.util.ArrayList;
import java.util.Collection;
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
        Collection<Access> accesses = new ArrayList<>();
        // TODO generate more accesses
        accesses.add(new Access(base.getWidth() / 2, base.getHeight() / 2));
        return new PatternIterator(base, accesses, constraints);
    }
}
