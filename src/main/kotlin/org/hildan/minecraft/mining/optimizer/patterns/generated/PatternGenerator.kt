package org.hildan.minecraft.mining.optimizer.patterns.generated

import org.hildan.minecraft.mining.optimizer.blocks.BlockType
import org.hildan.minecraft.mining.optimizer.blocks.Sample
import org.hildan.minecraft.mining.optimizer.geometry.Dimensions
import org.hildan.minecraft.mining.optimizer.patterns.Access
import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern
import java.util.ArrayDeque
import java.util.Deque
import java.util.HashSet
import java.util.NoSuchElementException

/** Constraints to limit the number of generated patterns. */
data class GenerationConstraints(val maxDimensions: Dimensions, val maxDugBlocks: Int) {
    override fun toString(): String = "max $maxDugBlocks dug blocks"
}

/** Generates digging patterns. */
class PatternGenerator(private val constraints: GenerationConstraints) : Iterable<DiggingPattern> {

    override fun iterator(): Iterator<DiggingPattern> {
        val maxDimensions = constraints.maxDimensions
        val accesses = listOf(Access(maxDimensions.width / 2, maxDimensions.height / 2))
        return PatternIterator(accesses, constraints)
    }
}

/** Enumerates all possible patterns within the given constraints. */
private class PatternIterator(
    accesses: Collection<Access>,
    private val constraints: GenerationConstraints
) : Iterator<DiggingPattern> {

    private val testSample: Sample = Sample(constraints.maxDimensions, BlockType.STONE)

    private val exploredStates: MutableSet<DiggingState> = HashSet(50)

    private val statesToExplore: Deque<DiggingState> = ArrayDeque<DiggingState>(25).apply {
        add(initialState(accesses))
    }

    override fun hasNext(): Boolean = !statesToExplore.isEmpty()

    override fun next(): DiggingPattern {
        val state = statesToExplore.pollFirst() ?: throw NoSuchElementException("no more patterns available")

        // never explore this state again
        exploredStates.add(state)

        // expand to find other states to explore
        testSample.fill(BlockType.STONE)
        state.replayOn(testSample)
        val newStates = state.expand(testSample, constraints)
        statesToExplore.addAll(newStates.filterNot(exploredStates::contains))

        return state.toPattern()
    }
}
