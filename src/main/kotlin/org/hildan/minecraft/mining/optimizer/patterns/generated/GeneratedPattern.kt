package org.hildan.minecraft.mining.optimizer.patterns.generated

import org.hildan.minecraft.mining.optimizer.blocks.Sample
import org.hildan.minecraft.mining.optimizer.geometry.BlockIndices
import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern

/**
 * A pattern that can be programmatically generated.
 */
internal class GeneratedPattern(
    private val dugPositions: BlockIndices
) : DiggingPattern {

    override fun digInto(sample: Sample) {
        // using for loop because way faster than forEach() and we repeat that a lot
        for (it in dugPositions) {
            sample.digBlock(it)
        }
    }
}
