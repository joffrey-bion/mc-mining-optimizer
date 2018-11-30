package org.hildan.minecraft.mining.optimizer.blocks

import org.hildan.minecraft.mining.optimizer.geometry.Dimensions
import org.hildan.minecraft.mining.optimizer.geometry.Distance3D
import org.hildan.minecraft.mining.optimizer.geometry.ONE_ABOVE
import org.hildan.minecraft.mining.optimizer.geometry.ONE_BELOW
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
    fun getBlock(position: Position): Block = blocks[dimensions.getIndex(position)]

    /**
     * Gets the [Block] located at the given [distance] from the given [origin] position.
     *
     * @param origin the position to start from
     * @param distance the 3D distance to travel (each dimension may be negative)
     * @param wrapping defines the decision to take when reaching the edge of the sample
     * @return the Block located at the resulting position, or null if the block is out of bounds and wrapping is set to
     * [Wrapping.CUT].
     */
    fun getBlock(origin: Position, distance: Distance3D, wrapping: Wrapping): Block? =
        dimensions.getIndex(origin, distance, wrapping)?.let { blocks[it] }

    /**
     * Gets the block above the given position.
     *
     * @param position the position above which to get a block
     * @param wrapping the wrapping policy when the given block is the ceiling of this sample
     * @return the above block, or null if the given block is the ceiling of this sample and wrapping is set to [Wrapping.CUT]
     */
    fun getBlockAbove(position: Position, wrapping: Wrapping): Block? = getBlock(position, ONE_ABOVE, wrapping)

    /**
     * Gets the block below the given one.
     *
     * @param position the position below which to get a block
     * @param wrapping the wrapping policy when the given block is the floor of this sample
     * @return the block below, or null if the given block is the floor of this sample and wrapping is set to [Wrapping.CUT]
     */
    fun getBlockBelow(position: Position, wrapping: Wrapping): Block? = getBlock(position, ONE_BELOW, wrapping)

    /**
     * Gets the 6 blocks that are adjacent to given position. If wrapping is set to [Wrapping.CUT] and the given
     * position is on this sample's edge, less than 6 blocks are returned because part of them is cut off.
     *
     * @param position the position to get the neighbors from
     * @param wrapping the wrapping policy when the given block is on the side of this chunk
     * @return a list containing blocks adjacent to the given position
     */
    fun getAdjacentBlocks(position: Position, wrapping: Wrapping): List<Block> =
        dimensions.getAdjacentIndices(position, wrapping).map { blocks[it] }

    /**
     * Changes the type of the block at the given [x], [y], [z] coordinates.
     */
    fun setBlock(x: Int, y: Int, z: Int, type: BlockType) = changeType(getBlock(x, y, z), type)

    fun fill(blockType: BlockType) = blocks.forEach { changeType(it, blockType) }

    /**
     * Digs the block at the specified [position].
     */
    fun digBlock(position: Position) = digBlock(position.x, position.y, position.z)

    /**
     * Digs the block at the specified [x], [y], [z] coordinates.
     */
    fun digBlock(x: Int, y: Int, z: Int) {
        setBlock(x, y, z, BlockType.AIR)

        // TODO move visibility logic to external visitor
        val block = getBlock(x, y, z)
        block.isVisible = true
        getAdjacentBlocks(block, Wrapping.WRAP).forEach { b -> b.isVisible = true }
    }

    fun digVisibleOres() {
        blocks.filter { it: Block -> it.isOre && it.isVisible }.forEach { digBlockAndAdjacentOres(it) }
    }

    private fun digBlockAndAdjacentOres(block: Block) {
        digBlock(block.x, block.y, block.z)
        for (ab in getAdjacentBlocks(block, Wrapping.WRAP_XZ)) {
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
