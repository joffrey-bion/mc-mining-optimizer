package org.hildan.minecraft.mining.optimizer.blocks

import org.hildan.minecraft.mining.optimizer.geometry.Dimensions
import org.hildan.minecraft.mining.optimizer.geometry.Position
import java.util.NoSuchElementException

/**
 * An arbitrary group of blocks. It can have any dimension, thus it is different from a minecraft chunk, which is
 * 16x256x16.
 */
class Sample {

    /**
     * The width of this sample. This dimension is related to the X coordinate.
     */
    val width: Int

    /**
     * The height of this sample. This dimension is related to the Y coordinate.
     */
    val height: Int

    /**
     * The length of this sample. This dimension is related to the Z coordinate.
     */
    val length: Int

    /**
     * The blocks of this sample.
     */
    val blocks: List<Block>

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
    constructor(dimensions: Dimensions, initialBlockType: BlockType) {
        this.width = dimensions.width
        this.height = dimensions.height
        this.length = dimensions.length
        this.blocks = mutableListOf()
        // initialize with stone blocks
        for (z in 0 until length) {
            for (y in 0 until height) {
                for (x in 0 until width) {
                    blocks.add(Block(x, y, z, initialBlockType))
                }
            }
        }
        oreBlocksCount = if (initialBlockType.isOre) blocks.size else 0
        dugBlocksCount = if (initialBlockType == BlockType.AIR) blocks.size else 0
    }

    /**
     * Creates a copy of the given Sample.
     *
     * @param source the Sample to copy
     */
    constructor(source: Sample) {
        this.width = source.width
        this.height = source.height
        this.length = source.length
        this.blocks = source.blocks.map { it.copy() }
        this.oreBlocksCount = source.oreBlocksCount
        this.dugBlocksCount = source.dugBlocksCount
    }

    /**
     * Returns whether the given coordinates belong to this chunk.
     *
     * @param x the X coordinate to test
     * @param y the Y coordinate to test
     * @param z the Z coordinate to test
     * @return true if the given coordinates belong to this chunk.
     */
    fun hasBlock(x: Int, y: Int, z: Int) = x >= 0 && y >= 0 && z >= 0 && x < width && y < height && z < length

    /**
     * Returns whether the given position belong to this chunk.
     *
     * @param position the position to test
     * @return true if the given position belong to this chunk.
     */
    fun hasBlock(position: Position) = hasBlock(position.x, position.y, position.z)

    /**
     * Returns the index in the internal array of the block at the given position.
     *
     * @param x the X coordinate of the block to get the index of
     * @param y the Y coordinate of the block to get the index of
     * @param z the Z coordinate of the block to get the index of
     * @return the index of the given block in the internal array [blocks]
     */
    private fun getIndex(x: Int, y: Int, z: Int): Int {
        if (!hasBlock(x, y, z)) {
            throw NoSuchElementException("Block ($x,$y,$z) does not exist in this sample")
        }
        return x + y * width + z * width * height
    }

    /**
     * Gets the Block located at the given absolute position.
     *
     * @param x the X coordinate of the block to get
     * @param y the Y coordinate of the block to get
     * @param z the Z coordinate of the block to get
     * @return the Block located at the provided coordinates
     */
    fun getBlock(x: Int, y: Int, z: Int): Block = blocks[getIndex(x, y, z)]

    /**
     * Gets the Block located at the given absolute position.
     *
     * @param position the absolute position of the block to get
     * @return the Block located at the provided coordinates
     */
    fun getBlock(position: Position): Block = getBlock(position.x, position.y, position.z)

    /**
     * Gets the Block located at the given relative position.
     *
     * @param origin the position to start from
     * @param distanceX the distance to travel in the X direction (may be negative)
     * @param distanceY the distance to travel in the Y direction (may be negative)
     * @param distanceZ the distance to travel in the Z direction (may be negative)
     * @param wrapping defines the decision to take when reaching the edge of the sample
     * @return the Block located at the given position, or null if the block is out of bounds and wrapping is set to
     * [Wrapping.CUT].
     */
    fun getBlock(origin: Position, distanceX: Int, distanceY: Int, distanceZ: Int, wrapping: Wrapping): Block? =
        when (wrapping) {
            Wrapping.CUT -> {
                val x = origin.x + distanceX
                val y = origin.y + distanceY
                val z = origin.z + distanceZ
                if (hasBlock(x, y, z)) getBlock(x, y, z) else null
            }
            Wrapping.WRAP -> {
                val x = Math.floorMod(origin.x + distanceX, width)
                val y = Math.floorMod(origin.y + distanceY, height)
                val z = Math.floorMod(origin.z + distanceZ, length)
                getBlock(x, y, z)
            }
        }

    /**
     * Gets the block above the given position.
     *
     * @param position the position above which to get a block
     * @param wrapping the wrapping policy when the given block is the ceiling of this sample
     * @return the above block, or null if the given block is the ceiling of this sample and wrapping is set to [ ][Wrapping.CUT]
     */
    fun getBlockAbove(position: Position, wrapping: Wrapping): Block? = getBlock(position, 0, 1, 0, wrapping)

    /**
     * Gets the block below the given one.
     *
     * @param position the position below which to get a block
     * @param wrapping the wrapping policy when the given block is the floor of this sample
     * @return the above block, or null if the given block is the floor of this sample and wrapping is set to [ ][Wrapping.CUT]
     */
    fun getBlockBelow(position: Position, wrapping: Wrapping): Block? = getBlock(position, 0, -1, 0, wrapping)

    /**
     * Gets the 6 blocks that are adjacent to given position. If wrapping is set to [Wrapping.CUT] and the given
     * position is on this sample's edge, less than 6 blocks are returned because part of them is cut off.
     *
     * @param position the position to get the neighbors from
     * @param wrapping the wrapping policy when the given block is on the side of this chunk
     * @return a list containing blocks adjacent to the given position
     */
    fun getAdjacentBlocks(position: Position, wrapping: Wrapping): List<Block> {
        val adjacentBlocks = mutableListOf<Block>()
        adjacentBlocks.addIfNotNull(getBlock(position, +1, 0, 0, wrapping))
        adjacentBlocks.addIfNotNull(getBlock(position, -1, 0, 0, wrapping))
        adjacentBlocks.addIfNotNull(getBlock(position, 0, +1, 0, wrapping))
        adjacentBlocks.addIfNotNull(getBlock(position, 0, -1, 0, wrapping))
        adjacentBlocks.addIfNotNull(getBlock(position, 0, 0, +1, wrapping))
        adjacentBlocks.addIfNotNull(getBlock(position, 0, 0, -1, wrapping))
        return adjacentBlocks
    }

    private fun MutableList<Block>.addIfNotNull(b: Block?) {
        if (b != null) {
            this.add(b)
        }
    }

    /**
     * Returns the collection of all blocks matching the given predicate in this sample.
     *
     * @param predicate the predicate to test the blocks
     * @return the collection of all blocks matching the given predicate in this sample.
     */
    inline fun getBlocksMatching(predicate: (Block) -> Boolean): List<Block> = blocks.filter(predicate)

    /**
     * Changes the type of the block at the given position.
     *
     * @param x the X coordinate of the block to change
     * @param y the Y coordinate of the block to change
     * @param z the Z coordinate of the block to change
     * @param type the new type of the block
     */
    fun setBlock(x: Int, y: Int, z: Int, type: BlockType) = changeType(getBlock(x, y, z), type)

    fun fill(blockType: BlockType) = blocks.forEach { changeType(it, blockType) }

    /**
     * Digs the block at the specified position.
     *
     * @param position the position to dig at
     */
    fun digBlock(position: Position) = digBlock(position.x, position.y, position.z)

    /**
     * Digs the block at the specified position.
     *
     * @param x the X coordinate of the block to dig
     * @param y the Y coordinate of the block to dig
     * @param z the Z coordinate of the block to dig
     */
    fun digBlock(x: Int, y: Int, z: Int) {
        setBlock(x, y, z, BlockType.AIR)

        // TODO move visibility logic to external visitor
        val block = getBlock(x, y, z)
        block.isVisible = true
        getAdjacentBlocks(block, Wrapping.WRAP).forEach { b -> b.isVisible = true }
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

    override fun toString(): String = "Size: $width $height $length  Dug: $dugBlocksCount"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Sample

        if (width != other.width) return false
        if (height != other.height) return false
        if (length != other.length) return false
        if (blocks != other.blocks) return false

        return true
    }

    override fun hashCode(): Int {
        var result = width
        result = 31 * result + height
        result = 31 * result + length
        result = 31 * result + blocks.hashCode()
        return result
    }
}
