package org.hildan.minecraft.mining.optimizer.patterns.generated

import org.hildan.minecraft.mining.optimizer.blocks.Sample
import org.hildan.minecraft.mining.optimizer.geometry.Dimensions
import org.hildan.minecraft.mining.optimizer.geometry.Position
import org.hildan.minecraft.mining.optimizer.patterns.Access
import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern

/**
 * A pattern that can be programmatically generated.
 */
internal class GeneratedPattern(
    private val accesses: Set<Access>,
    private val dugPositions: List<Position>
) : DiggingPattern {

    override fun getAccesses(dimensions: Dimensions) = accesses

    override fun digInto(sample: Sample) = dugPositions.forEach { sample.digBlock(it) }
}
