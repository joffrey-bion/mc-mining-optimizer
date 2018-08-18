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
     * Returns whether the given coordinates fit in these dimensions.
     *
     * @param x the X coordinate to test
     * @param y the Y coordinate to test
     * @param z the Z coordinate to test
     * @return true if the given coordinates fit in these dimensions.
     */
    fun contains(x: Int, y: Int, z: Int) = x >= 0 && y >= 0 && z >= 0 && x < width && y < height && z < length

    override fun toString(): String = "${width}x${height}x$length"
}
