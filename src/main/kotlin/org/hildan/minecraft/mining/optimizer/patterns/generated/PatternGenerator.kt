package org.hildan.minecraft.mining.optimizer.patterns.generated

import org.hildan.minecraft.mining.optimizer.patterns.Access
import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern

/**
 * Generates digging patterns.
 */
class PatternGenerator(
    private val constraints: GenerationConstraints
) : Iterable<DiggingPattern> {

    override fun iterator(): Iterator<DiggingPattern> {
        val maxDimensions = constraints.maxDimensions
        val accesses = listOf(Access(maxDimensions.width / 2, maxDimensions.height / 2))
        return PatternIterator(accesses, constraints)
    }
}
