package org.hildan.minecraft.mining.optimizer.patterns.generated;

import org.hildan.minecraft.mining.optimizer.chunks.Sample;
import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern;

import java.util.Iterator;

/**
 * Generates digging patterns.
 */
public class PatternGenerator implements Iterable<DiggingPattern> {

    private final Sample base;
    private final int maxActions;

    private final int maxDugBlocks;

    public PatternGenerator(Sample base, int maxActions, int maxDugBlocks) {
        this.base = base;
        this.maxActions = maxActions;
        this.maxDugBlocks = maxDugBlocks;
    }

    @Override
    public Iterator<DiggingPattern> iterator() {
        // FIXME generate accesses?
        return new PatternIterator(base, null, maxActions, maxDugBlocks);
    }
}
