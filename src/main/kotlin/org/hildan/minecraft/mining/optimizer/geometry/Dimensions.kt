package org.hildan.minecraft.mining.optimizer.geometry

import org.hildan.minecraft.mining.optimizer.blocks.Wrapping

/**
 * Represents an immutable 3D dimensions specification.
 */
data class Dimensions(
    val width: Int,
    val height: Int,
    val length: Int
) {
    /**
     * Returns whether the given coordinates fit in these dimensions.
     *
     * @param x the X coordinate to test
     * @param y the Y coordinate to test
     * @param z the Z coordinate to test
     * @return true if the given coordinates fit in these dimensions.
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
        }

    override fun toString(): String = "${width}x${height}x$length"
}
