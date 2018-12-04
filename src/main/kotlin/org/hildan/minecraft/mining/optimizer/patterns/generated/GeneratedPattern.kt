package org.hildan.minecraft.mining.optimizer.patterns.generated

import org.hildan.minecraft.mining.optimizer.blocks.Sample
import org.hildan.minecraft.mining.optimizer.geometry.BlockIndex
import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern

/**
 * A pattern that can be programmatically generated.
 */
internal class GeneratedPattern(
    private val dugPositions: Set<BlockIndex>
) : DiggingPattern {
    override fun digInto(sample: Sample) = dugPositions.forEach { sample.digBlock(it) }
}
