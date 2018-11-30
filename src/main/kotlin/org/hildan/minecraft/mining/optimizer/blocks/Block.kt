package org.hildan.minecraft.mining.optimizer.blocks

import org.hildan.minecraft.mining.optimizer.geometry.Position

/**
 * Represents a Minecraft block.
 */
class Block(
    override val x: Int,
    override val y: Int,
    override val z: Int,
    var type: BlockType
) : Position {

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
    internal fun copy(): Block {
        val copy = Block(x, y, z, type)
        copy.isVisible = isVisible
        copy.isExplored = isExplored
        return copy
    }

    fun resetTo(refBlock: Block) {
        type = refBlock.type
        isVisible = refBlock.isVisible
        isExplored = refBlock.isExplored
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Block

        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false
        if (type != other.type) return false
        if (isVisible != other.isVisible) return false
        if (isExplored != other.isExplored) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        result = 31 * result + z
        result = 31 * result + type.hashCode()
        result = 31 * result + isVisible.hashCode()
        result = 31 * result + isExplored.hashCode()
        return result
    }

    override fun toString(): String = type.toString()
}
