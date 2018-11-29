package org.hildan.minecraft.mining.optimizer.geometry

/**
 * Represents an immutable 3D dimensions specification.
 */
data class Dimensions(
    val width: Int,
    val height: Int,
    val length: Int
) {
    /**
     * Returns whether the given [x], [y] and [z] coordinates fit in these dimensions.
     */
    fun contains(x: Int, y: Int, z: Int) = x >= 0 && y >= 0 && z >= 0 && x < width && y < height && z < length

    fun getPos(origin: Position, distanceX: Int, distanceY: Int, distanceZ: Int, wrapping: Wrapping): Position? =
        when (wrapping) {
            Wrapping.CUT -> {
                val x = origin.x + distanceX
                val y = origin.y + distanceY
                val z = origin.z + distanceZ
                if (contains(x, y, z)) Position.of(x, y, z) else null
            }
            Wrapping.WRAP -> {
                val x = Math.floorMod(origin.x + distanceX, width)
                val y = Math.floorMod(origin.y + distanceY, height)
                val z = Math.floorMod(origin.z + distanceZ, length)
                Position.of(x, y, z)
            }
            Wrapping.WRAP_XZ -> {
                val x = Math.floorMod(origin.x + distanceX, width)
                val y = origin.y + distanceY
                val z = Math.floorMod(origin.z + distanceZ, length)
                if (contains(x, y, z)) Position.of(x, y, z) else null
            }
        }

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
