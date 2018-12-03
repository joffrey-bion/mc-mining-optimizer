package org.hildan.minecraft.mining.optimizer.blocks

import org.hildan.minecraft.mining.optimizer.geometry.BlockIndex
import org.hildan.minecraft.mining.optimizer.geometry.Dimensions
import org.hildan.minecraft.mining.optimizer.geometry.Position
import org.hildan.minecraft.mining.optimizer.geometry.Wrapping

/**
 * An arbitrary group of blocks. It can have any dimension, thus it is different from a minecraft chunk, which is
 * 16x256x16.
 */
data class Sample(
    /**
     * The dimensions of this sample.
     */
    val dimensions: Dimensions,
    /**
     * The blocks of this sample.
     */
    private val blocks: List<Block>
) {
    /**
     * The number of ore blocks currently in this sample.
     */
    var oreBlocksCount = 0
        private set

    /**
     * The number of dug blocks currently in this sample.
     */
    var dugBlocksCount = 0
        private set

    /**
     * Creates a new pure stone sample of the given dimensions.
     */
    constructor(dimensions: Dimensions, initialBlockType: BlockType) : this(
        dimensions,
        createBlocks(dimensions, initialBlockType)
    ) {
        oreBlocksCount = if (initialBlockType.isOre) blocks.size else 0
        dugBlocksCount = if (initialBlockType == BlockType.AIR) blocks.size else 0
    }

    /**
     * Creates a copy of the given Sample.
     *
     * @param source the Sample to copy
     */
    constructor(source: Sample) : this(source.dimensions, source.blocks.map { it.copy() }) {
        this.oreBlocksCount = source.oreBlocksCount
        this.dugBlocksCount = source.dugBlocksCount
    }

    /**
     * Returns whether the given [x], [y], [z] coordinates belong to this sample.
     */
    fun contains(x: Int, y: Int, z: Int) = dimensions.contains(x, y, z)

    /**
     * Returns whether the given [position] belong to this sample.
     */
    fun contains(position: Position) = contains(position.x, position.y, position.z)

    /**
     * Gets the [Block] located at the given [x], [y], [z] coordinates.
     */
    fun getBlock(x: Int, y: Int, z: Int): Block = blocks[dimensions.getIndex(x, y, z)]

    /**
     * Gets the [Block] located at the given absolute [position].
     */
    fun getBlock(position: Position): Block = with(dimensions) { blocks[position.index] }

    /**
     * Gets the 6 blocks that are adjacent to given position. If wrapping is set to [Wrapping.CUT] and the given
     * position is on this sample's edge, less than 6 blocks are returned because part of them is cut off.
     *
     * @param position the position to get the neighbors from
     * @param wrapping the wrapping policy when the given block is on the side of this chunk
     * @return a list containing blocks adjacent to the given position
     */
    fun getAdjacentBlocks(position: Position, wrapping: Wrapping = Wrapping.WRAP_XZ): List<Block> =
        dimensions.getAdjacentIndices(position, wrapping).map { blocks[it] }

    /**
     * Changes the type of the block at the given [x], [y], [z] coordinates.
     */
    fun setBlock(x: Int, y: Int, z: Int, type: BlockType) = changeType(getBlock(x, y, z), type)

    fun fill(blockType: BlockType) = blocks.forEach { changeType(it, blockType) }

    /**
     * Digs the block at the specified [index].
     */
    fun digBlock(index: BlockIndex) {
        val block = blocks[index]
        changeType(block, BlockType.AIR)

        // TODO move visibility logic to external visitor
        block.isVisible = true
        getAdjacentBlocks(block).forEach { b -> b.isVisible = true }
    }

    /**
     * Digs the block at the specified [x], [y], [z] coordinates.
     */
    fun digBlock(x: Int, y: Int, z: Int) = digBlock(dimensions.getIndex(x, y, z))

    fun digVisibleOres() {
        blocks.filter { it: Block -> it.isOre && it.isVisible }.forEach { digBlockAndAdjacentOres(it) }
    }

    private fun digBlockAndAdjacentOres(block: Block) {
        digBlock(block.x, block.y, block.z)
        for (ab in getAdjacentBlocks(block)) {
            if (ab.isOre && ab.isVisible) {
                digBlockAndAdjacentOres(ab)
            }
        }
    }

    fun resetTo(sample: Sample) {
        assert(dimensions == sample.dimensions) {
            "the given sample does not have the same dimensions as this one"
        }
        for (i in blocks.indices) {
            blocks[i].resetTo(sample.blocks[i])
        }
        oreBlocksCount = sample.oreBlocksCount
        dugBlocksCount = sample.dugBlocksCount
    }

    private fun changeType(block: Block, type: BlockType) {
        val formerType = block.type
        block.type = type
        if (!formerType.isOre && type.isOre) {
            oreBlocksCount++
        } else if (formerType.isOre && !type.isOre) {
            oreBlocksCount--
        }
        if (formerType != BlockType.AIR && type == BlockType.AIR) {
            dugBlocksCount++
        } else if (formerType == BlockType.AIR && type != BlockType.AIR) {
            dugBlocksCount--
        }
    }

    override fun toString(): String = "Size: $dimensions  Dug: $dugBlocksCount"

    companion object {
        private fun createBlocks(dimensions: Dimensions, initialBlockType: BlockType): List<Block> =
            dimensions.positions.map { (Block(it.x, it.y, it.z, initialBlockType)) }
    }
}
