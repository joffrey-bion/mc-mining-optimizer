package org.hildan.minecraft.mining.optimizer.patterns.generated;

import org.hildan.minecraft.mining.optimizer.chunks.Sample;
import org.hildan.minecraft.mining.optimizer.geometry.Position;
import org.hildan.minecraft.mining.optimizer.patterns.Access;
import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Enumerates all possible patterns within the given limits.
 */
class PatternIterator implements Iterator<DiggingPattern> {

    private final int maxActions;

    private final int maxDugBlocks;

    private Set<DiggingState> exploredStates;

    private Deque<DiggingState> statesToExplore;

    public PatternIterator(Sample base, Access access, int maxActions, int maxDugBlocks) {
        this.maxActions = maxActions;
        this.maxDugBlocks = maxDugBlocks;
        this.statesToExplore = new ArrayDeque<>(25);

        // TODO handle multiple accesses?
        Position startingHeadPosition = access.above();
        Sample sample = new Sample(base);
        sample.digBlock(access);
        sample.digBlock(startingHeadPosition);
        DiggingState initialState = new DiggingState(startingHeadPosition, new Sample(base));
        statesToExplore.add(initialState);
    }

    @Override
    public boolean hasNext() {
        return !statesToExplore.isEmpty();
    }

    @Override
    public DiggingPattern next() {
        DiggingState state = statesToExplore.pollFirst();
        if (state == null) {
            throw new NoSuchElementException("no more patterns available");
        }

        // never explore this state again
        exploredStates.add(state);

        // FIXME add the maxActions and maxDugBlocks constraints here
        if (true) {
            // expand to find other states
            Collection<DiggingState> newStates = state.expand();

            // enqueue the new states to explore except if already explored
            newStates.removeIf(exploredStates::contains);
            statesToExplore.addAll(newStates);
        }
        return state.toPattern();
    }
}
