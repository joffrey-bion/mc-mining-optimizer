package org.hildan.minecraft.mining.optimizer.patterns.generated

import org.hildan.minecraft.mining.optimizer.geometry.BlockIndex
import org.hildan.minecraft.mining.optimizer.geometry.Dimensions
import org.hildan.minecraft.mining.optimizer.geometry.Position
import java.util.Arrays

class DigMatrix(
    val dimensions: Dimensions
) {
    val blocks: BooleanArray = BooleanArray(dimensions.nbPositions)

    fun dig(position: Position) = with(dimensions) {
        blocks[position.index] = true
    }

    fun isDug(block: BlockIndex): Boolean = blocks[block]

    fun isDugAbove(block: BlockIndex): Boolean = with(dimensions) {
        isDug(block.above!!)
    }

    fun isBlockOrBottomBelow(feetPosition: BlockIndex): Boolean = with(dimensions) {
        feetPosition.below?.let { !isDug(it) } ?: true
    }

    fun reset() = Arrays.fill(blocks, false)
}
