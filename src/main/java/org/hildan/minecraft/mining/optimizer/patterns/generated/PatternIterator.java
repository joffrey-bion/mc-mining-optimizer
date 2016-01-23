package org.hildan.minecraft.mining.optimizer.patterns.generated;

import org.hildan.minecraft.mining.optimizer.chunks.Sample;
import org.hildan.minecraft.mining.optimizer.patterns.Access;
import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Enumerates all possible patterns within the given constraints.
 */
class PatternIterator implements Iterator<DiggingPattern> {

    private final GenerationConstraints constraints;

    private final Set<DiggingState> exploredStates;

    private final Deque<DiggingState> statesToExplore;

    public PatternIterator(Sample initialSample, Collection<Access> accesses, GenerationConstraints constraints) {
        this.constraints = constraints;
        this.exploredStates = new HashSet<>(50);
        this.statesToExplore = new ArrayDeque<>(25);

        DiggingState initialState = new DiggingState(initialSample, accesses);
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

        // expand to find other states to explore
        Collection<DiggingState> newStates = state.expand(constraints);
        newStates.removeIf(exploredStates::contains);
        statesToExplore.addAll(newStates);

        return state.toPattern();
    }
}
