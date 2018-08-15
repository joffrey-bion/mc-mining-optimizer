package org.hildan.minecraft.mining.optimizer.blocks

import org.hildan.minecraft.mining.optimizer.geometry.Position

/**
 * Represents a Minecraft block.
 */
class Block(x: Int, y: Int, z: Int) : Position(x, y, z) {

    /** The type of this block. */
    var type: BlockType = BlockType.STONE

    var isVisible: Boolean = false

    var isExplored: Boolean = false

    /**
     * Whether this block has been dug.
     */
    val isDug: Boolean
        get() = type == BlockType.AIR

    /**
     * Whether this block is an ore block.
     */
    val isOre: Boolean
        get() = type.isOre

    /**
     * Creates a copy of this block.
     */
    internal fun copy() : Block {
        val copy = Block(x, y, z)
        copy.type = type
        copy.isVisible = isVisible
        copy.isExplored = isExplored
        return copy
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as Block

        if (type != other.type) return false
        if (isVisible != other.isVisible) return false
        if (isExplored != other.isExplored) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + isVisible.hashCode()
        result = 31 * result + isExplored.hashCode()
        return result
    }

    override fun toString(): String = type.toString()
}
