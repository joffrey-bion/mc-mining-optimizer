package org.hildan.minecraft.mining.optimizer.patterns.generated

import org.hildan.minecraft.mining.optimizer.blocks.Sample
import org.hildan.minecraft.mining.optimizer.patterns.Access
import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern

/**
 * Generates digging patterns.
 */
class PatternGenerator(
    private val base: Sample,
    private val constraints: GenerationConstraints
) : Iterable<DiggingPattern> {

    override fun iterator(): Iterator<DiggingPattern> {
        val accesses = listOf(Access(base.width / 2, base.height / 2))
        return PatternIterator(base, accesses, constraints)
    }
}
