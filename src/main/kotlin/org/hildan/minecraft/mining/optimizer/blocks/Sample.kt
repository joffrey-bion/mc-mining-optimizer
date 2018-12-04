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
    private val blocks: List<Block>
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
        createBlocks(dimensions, initialBlockType)
    ) {
        oreBlocksCount = if (initialBlockType.isOre) blocks.size else 0
        dugBlocksCount = if (initialBlockType == BlockType.AIR) blocks.size else 0
    }

    /**
     * Creates a copy of the given [source] Sample.
     */
    constructor(source: Sample) : this(source.dimensions, source.blocks.map { it.copy() }) {
        this.oreBlocksCount = source.oreBlocksCount
        this.dugBlocksCount = source.dugBlocksCount
    }

    /**
     * Gets the 6 blocks that are adjacent to this block, with [Wrapping.WRAP_XZ] wrapping. If this block is on the
     * floor on ceiling of this sample, less than 6 blocks are returned because part of them is cut off.
     */
    private val Block.neighbours
        get() = with(dimensions) { position.neighbours.map { blocks[it] } }

    /**
     * Returns whether the given [x], [y], [z] coordinates belong to this sample.
     */
    fun contains(x: Int, y: Int, z: Int) = dimensions.contains(x, y, z)

    /**
     * Gets the [Block] located at the given [x], [y], [z] coordinates.
     */
    fun getBlock(x: Int, y: Int, z: Int): Block = blocks[dimensions.getIndex(x, y, z)]

    /**
     * Changes the type of the block at the given [x], [y], [z] coordinates.
     */
    fun setBlock(x: Int, y: Int, z: Int, type: BlockType) = changeType(getBlock(x, y, z), type)

    fun fill(blockType: BlockType) = blocks.forEach { changeType(it, blockType) }

    /**
     * Digs the block at the specified [index].
     */
    fun digBlock(index: BlockIndex) {
        changeType(blocks[index], BlockType.AIR)
    }

    /**
     * Digs the block at the specified [x], [y], [z] coordinates.
     */
    fun digBlock(x: Int, y: Int, z: Int) = digBlock(dimensions.getIndex(x, y, z))

    fun digVisibleOresRecursively() {
        val dugBlocks=  blocks.filter { it.isDug }
        val explored=  dugBlocks.mapTo(HashSet()) { it.index }
        val toExplore = dugBlocks.flatMapTo(ArrayDeque()) { it.neighbours }
        while (toExplore.isNotEmpty()) {
            val block = toExplore.poll()
            explored.add(block.index)
            if (block.isOre) {
                digBlock(block.index)
                block.neighbours.filterNotTo(toExplore) { explored.contains(it.index) }
            }
        }
    }

    fun resetTo(sample: Sample) {
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
            with(dimensions) {
                positions.map { Block(it.index, it, initialBlockType) }
            }
    }
}
