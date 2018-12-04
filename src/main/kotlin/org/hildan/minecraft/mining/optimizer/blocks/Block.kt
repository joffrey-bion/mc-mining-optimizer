package org.hildan.minecraft.mining.optimizer.blocks

import org.hildan.minecraft.mining.optimizer.geometry.BlockIndex
import org.hildan.minecraft.mining.optimizer.geometry.Position
import org.hildan.minecraft.mining.optimizer.ore.BlockType

/**
 * Represents a Minecraft block.
 */
class Block(
    val index: BlockIndex,
    val position: Position,
    var type: BlockType
) {
    /** Whether this block has been dug. */
    val isDug: Boolean
        get() = type == BlockType.AIR

    /** Whether this block is an ore block. */
    val isOre: Boolean
        get() = type.isOre

    /**
     * Creates a copy of this block.
     */
    internal fun copy(): Block = Block(index, position, type)

    fun resetTo(refBlock: Block) {
        type = refBlock.type
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Block

        if (index != other.index) return false
        if (position != other.position) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = index
        result = 31 * result + position.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }

    override fun toString(): String = type.toString()
}
