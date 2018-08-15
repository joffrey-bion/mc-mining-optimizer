package org.hildan.minecraft.mining.optimizer.patterns.generated

import org.hildan.minecraft.mining.optimizer.blocks.Sample
import org.hildan.minecraft.mining.optimizer.patterns.Access
import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern

import java.util.ArrayDeque
import java.util.Deque
import java.util.HashSet
import java.util.NoSuchElementException

/**
 * Enumerates all possible patterns within the given constraints.
 */
internal class PatternIterator(
    initialSample: Sample,
    accesses: Collection<Access>,
    private val constraints: GenerationConstraints
) : Iterator<DiggingPattern> {

    private val exploredStates: MutableSet<DiggingState>

    private val statesToExplore: Deque<DiggingState>

    init {
        this.exploredStates = HashSet(50)
        this.statesToExplore = ArrayDeque(25)

        val initialState = DiggingState(initialSample, accesses)
        statesToExplore.add(initialState)
    }

    override fun hasNext(): Boolean {
        return !statesToExplore.isEmpty()
    }

    override fun next(): DiggingPattern {
        var state: DiggingState
        do {
            state = statesToExplore.pollFirst() ?: throw NoSuchElementException("no more patterns available")

            // never explore this state again
            exploredStates.add(state)

            // expand to find other states to explore
            val newStates = state.expand(constraints)
            statesToExplore.addAll(newStates.filterNot(exploredStates::contains))
        } while (!statesToExplore.isEmpty() && !state.isCanonical)

        return state.toPattern()
    }
}
