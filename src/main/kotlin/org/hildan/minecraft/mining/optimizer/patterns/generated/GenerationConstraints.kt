package org.hildan.minecraft.mining.optimizer.patterns.generated

import org.hildan.minecraft.mining.optimizer.geometry.Dimensions

/**
 * Represents constraints to limit the number of generated patterns.
 */
data class GenerationConstraints(
    val maxDimensions: Dimensions,
    val maxDugBlocks: Int
) {

    override fun toString(): String = "max $maxDugBlocks dug blocks"
}
