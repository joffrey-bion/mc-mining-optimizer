package org.hildan.minecraft.mining.optimizer.patterns.generated

import org.hildan.minecraft.mining.optimizer.geometry.BlockIndex
import org.hildan.minecraft.mining.optimizer.geometry.Dimensions
import java.util.Arrays

class DigMatrix(
    val dimensions: Dimensions
) {
    private val dugState: BooleanArray = BooleanArray(dimensions.nbPositions)

    fun dig(block: BlockIndex) {
        dugState[block] = true
    }

    fun isDug(block: BlockIndex): Boolean = dugState[block]

    fun isDugAbove(block: BlockIndex): Boolean = with(dimensions) {
        isDug(block.above!!)
    }

    fun isBlockOrBottomBelow(feetPosition: BlockIndex): Boolean = with(dimensions) {
        feetPosition.below?.let { !isDug(it) } ?: true
    }

    fun reset() = Arrays.fill(dugState, false)
}
