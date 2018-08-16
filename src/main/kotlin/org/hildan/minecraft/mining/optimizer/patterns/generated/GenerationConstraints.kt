package org.hildan.minecraft.mining.optimizer.patterns.generated

/**
 * Represents constraints to limit the number of generated patterns.
 */
data class GenerationConstraints(val maxActions: Int, val maxDugBlocks: Int) {

    override fun toString(): String = "max $maxActions actions, max $maxDugBlocks dug blocks"
}