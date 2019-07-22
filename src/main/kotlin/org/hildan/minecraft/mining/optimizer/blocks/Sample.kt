package org.hildan.minecraft.mining.optimizer.blocks

import org.hildan.minecraft.mining.optimizer.geometry.BlockIndex
import org.hildan.minecraft.mining.optimizer.geometry.Dimensions
import org.hildan.minecraft.mining.optimizer.geometry.Wrapping
import org.hildan.minecraft.mining.optimizer.ore.BlockType
import java.util.ArrayDeque

/**
 * An arbitrary group of blocks. It can have any dimension, thus it is different from a minecraft chunk, which is
 * 16x256x16.
 */
data class Sample(
    /** The dimensions of this sample. */
    val dimensions: Dimensions,
    /** The blocks of this sample. */
    private val blocks: MutableList<BlockType>
) {
    /** The number of ore blocks currently in this sample. */
    var oreBlocksCount = 0
        private set

    /** The number of dug blocks currently in this sample. */
    var dugBlocksCount = 0
        private set

    /**
     * Creates a new pure sample of the given [dimensions] containing only blocks of the given [initialBlockType].
     */
    constructor(dimensions: Dimensions, initialBlockType: BlockType) : this(
        dimensions,
        MutableList(dimensions.nbPositions) { initialBlockType }
    ) {
        oreBlocksCount = if (initialBlockType.isOre) blocks.size else 0
        dugBlocksCount = if (initialBlockType == BlockType.AIR) blocks.size else 0
    }

    /**
     * Creates a copy of the given [source] Sample.
     */
    constructor(source: Sample) : this(
        source.dimensions,
        ArrayList(source.blocks)
    ) {
        this.oreBlocksCount = source.oreBlocksCount
        this.dugBlocksCount = source.dugBlocksCount
    }

    /**
     * Gets the 6 blocks that are adjacent to this block, with [Wrapping.WRAP_XZ] wrapping. If this block is on the
     * floor or ceiling of this sample, less than 6 blocks are returned because part of them is cut off.
     */
    private val BlockIndex.neighbours
        get() = with(dimensions) { neighbours }

    /**
     * Returns whether the given [x], [y], [z] coordinates belong to this sample.
     */
    fun contains(x: Int, y: Int, z: Int): Boolean = dimensions.contains(x, y, z)

    /**
     * Gets the type of the block located at the given [x], [y], [z] coordinates.
     */
    fun getBlockType(x: Int, y: Int, z: Int): BlockType = getBlockType(getIndex(x, y, z))

    /**
     * Sets the type of the block located at the given [x], [y], [z] coordinates.
     */
    fun setBlockType(x: Int, y: Int, z: Int, type: BlockType) = setBlockType(getIndex(x, y, z), type)

    private fun getIndex(x: Int, y: Int, z: Int) = dimensions.getIndex(x, y, z)

    /**
     * Gets the type of the block located at the given [index].
     */
    private fun getBlockType(index: BlockIndex): BlockType = blocks[index]

    /**
     * Sets the type of the block located at the given [index].
     */
    private fun setBlockType(index: BlockIndex, type: BlockType) {
        val formerType = blocks[index]
        blocks[index] = type
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

    fun fill(blockType: BlockType) {
        blocks.fill(blockType)
        dugBlocksCount = if (blockType == BlockType.AIR) blocks.size else 0
        oreBlocksCount = if (blockType.isOre) blocks.size else 0
    }

    /**
     * Digs the block at the specified [index].
     */
    fun digBlock(index: BlockIndex) {
        setBlockType(index, BlockType.AIR)
    }

    /**
     * Digs the block at the specified [x], [y], [z] coordinates.
     */
    fun digBlock(x: Int, y: Int, z: Int) = digBlock(getIndex(x, y, z))

    fun digVisibleOresRecursively() {
        val explored= findDugBlocksIndices()
        val toExplore = explored.flatMapTo(ArrayDeque()) { it.neighbours.asIterable() }
        while (toExplore.isNotEmpty()) {
            val blockIndex = toExplore.poll()
            explored.add(blockIndex)
            if (blocks[blockIndex].isOre) {
                digBlock(blockIndex)
                blockIndex.neighbours.filterNotTo(toExplore) { explored.contains(it) }
            }
        }
    }

    private fun findDugBlocksIndices(): HashSet<Int> =
            blocks.mapIndexedNotNullTo(HashSet()) { index, type -> if (type == BlockType.AIR) index else null }

    fun resetTo(sample: Sample) {
        for (i in blocks.indices) {
            blocks[i] = sample.blocks[i]
        }
        oreBlocksCount = sample.oreBlocksCount
        dugBlocksCount = sample.dugBlocksCount
    }

    override fun toString(): String = "Size: $dimensions  Dug: $dugBlocksCount"
}
