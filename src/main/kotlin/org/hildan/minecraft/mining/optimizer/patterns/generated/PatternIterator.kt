package org.hildan.minecraft.mining.optimizer.patterns.generated

import org.hildan.minecraft.mining.optimizer.blocks.BlockType
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
    accesses: Collection<Access>,
    private val constraints: GenerationConstraints
) : Iterator<DiggingPattern> {

    private val exploredStates: MutableSet<DiggingState> = HashSet(50)

    private val statesToExplore: Deque<DiggingState> = ArrayDeque(25)

    private val testSample: Sample = Sample(constraints.maxDimensions, BlockType.STONE)

    init {
        val initialState = DiggingState(accesses)
        statesToExplore.add(initialState)
    }

    override fun hasNext(): Boolean = !statesToExplore.isEmpty()

    override fun next(): DiggingPattern {
        var state: DiggingState
        do {
            state = statesToExplore.pollFirst() ?: throw NoSuchElementException("no more patterns available")

            // never explore this state again
            exploredStates.add(state)

            // expand to find other states to explore
            testSample.fill(BlockType.STONE)
            state.replayOn(testSample)
            val newStates = state.expand(testSample, constraints)
            statesToExplore.addAll(newStates.filterNot(exploredStates::contains))
        } while (!statesToExplore.isEmpty() && !state.isCanonical)

        return state.toPattern(testSample.width, testSample.height, testSample.length)
    }
}
