package org.hildan.minecraft.mining.optimizer.geometry

import java.util.EnumMap

/**
 * The index of a block in 1-dimensional array representing a 3D matrix.
 */
typealias BlockIndex = Int

/**
 * An array of [BlockIndex].
 */
typealias BlockIndices = IntArray

/**
 * Represents 3D dimensions and allows to map 3D [Position]s to [BlockIndex] / [BlockIndices].
 */
data class Dimensions(
    val width: Int,
    val height: Int,
    val length: Int
) {
    val nbPositions = width * height * length

    val positions: List<Position> = ArrayList<Position>(nbPositions).apply {
        for (z in 0 until length) {
            for (y in 0 until height) {
                for (x in 0 until width) {
                    add(Position.of(x, y, z))
                }
            }
        }
    }

    private val adjacentIndices: EnumMap<Wrapping, Map<Position, BlockIndices>> =
            Wrapping.values().associateTo(EnumMap(Wrapping::class.java)) { it to findAdjacentIndices(positions, it) }

    private fun findAdjacentIndices(positions: List<Position>, wrapping: Wrapping): Map<Position, BlockIndices> =
            positions.associateWith { findAdjacentIndices(it, wrapping) }

    private fun findAdjacentIndices(position: Position, wrapping: Wrapping): BlockIndices = mutableListOf<Int>().apply {
        addIfNotNull(getIndex(position, ONE_EAST, wrapping))
        addIfNotNull(getIndex(position, ONE_WEST, wrapping))
        addIfNotNull(getIndex(position, ONE_ABOVE, wrapping))
        addIfNotNull(getIndex(position, ONE_BELOW, wrapping))
        addIfNotNull(getIndex(position, ONE_NORTH, wrapping))
        addIfNotNull(getIndex(position, ONE_SOUTH, wrapping))
    }.toIntArray()

    private fun MutableList<Int>.addIfNotNull(b: Int?) {
        if (b != null) {
            this.add(b)
        }
    }

    /**
     * Returns whether the given [x], [y] and [z] coordinates fit in these dimensions.
     */
    fun contains(x: Int, y: Int, z: Int) = 0 <= x && 0 <= y && 0 <= z && x < width && y < height && z < length

    val BlockIndex.above
        get() = this + ONE_ABOVE

    val BlockIndex.below
        get() = this + ONE_BELOW

    operator fun BlockIndex.plus(distance: Distance3D): BlockIndex? = getIndex(positions[this], distance)

    /** The index (in a position array) of this position. */
    val Position.index
        get() = getIndex(x, y, z)

    val Position.neighbours
        get() = adjacentIndices[Wrapping.WRAP_XZ]!![this]!!

    operator fun Position.plus(distance: Distance3D): Position? = getIndex(this, distance)?.let { positions[it] }

    private fun getIndex(origin: Position, distance: Distance3D, wrapping: Wrapping = Wrapping.WRAP_XZ): BlockIndex? {
        return when (wrapping) {
            Wrapping.CUT -> {
                val x = origin.x + distance.x
                val y = origin.y + distance.y
                val z = origin.z + distance.z
                getIndexIfValid(x, y, z)
            }
            Wrapping.WRAP -> {
                val x = Math.floorMod(origin.x + distance.x, width)
                val y = Math.floorMod(origin.y + distance.y, height)
                val z = Math.floorMod(origin.z + distance.z, length)
                getIndex(x, y, z)
            }
            Wrapping.WRAP_XZ -> {
                val x = Math.floorMod(origin.x + distance.x, width)
                val y = origin.y + distance.y
                val z = Math.floorMod(origin.z + distance.z, length)
                getIndexIfValid(x, y, z)
            }
        }
    }

    private fun getIndexIfValid(x: Int, y: Int, z: Int): BlockIndex? =
        if (contains(x, y, z)) getIndex(x, y, z) else null

    /**
     * Returns the index (in a position array) corresponding to the given [x], [y], [z] coordinates.
     */
    fun getIndex(x: Int, y: Int, z: Int): BlockIndex {
        if (!contains(x, y, z)) {
            throw NoSuchElementException("Position ($x,$y,$z) does not exist in this sample")
        }
        return x + y * width + z * width * height
    }

    fun getAdjacentIndices(position: Position, wrapping: Wrapping = Wrapping.WRAP_XZ): BlockIndices =
        adjacentIndices[wrapping]!![position]!!

    override fun toString(): String = "${width}x${height}x$length"
}

/**
 * Defines how some functions behave when accessing positions outside of some [Dimensions].
 */
enum class Wrapping {
    /**
     * Does not consider blocks that are outside the sample.
     */
    CUT,
    /**
     * Wraps to the other side of the sample. This means that when reaching for instance a Y value of 1 above the height
     * of the sample, it wraps it back to Y=0.
     */
    WRAP,
    /**
     * Only wraps horizontally (on X and Z) but not vertically (Y).
     *
     * Behaves like [WRAP] when reaching the sides of the sample, but like [CUT] when reaching the ceiling/floor.
     *
     * This is useful when doing digging-related stuff because the probabilities of finding ores only depend on the
     * vertical (Y) position. Therefore, wrapping horizontally is valid because digging the next sample gives the same
     * kind  of results as digging the other side of the same sample, but only when these samples are side by side, not
     * on top of each other.
     */
    WRAP_XZ
}
