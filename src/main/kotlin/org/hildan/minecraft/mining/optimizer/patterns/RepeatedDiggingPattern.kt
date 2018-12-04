package org.hildan.minecraft.mining.optimizer.patterns

import org.hildan.minecraft.mining.optimizer.blocks.Sample

/**
 * Represents a DiggingPattern that is repeated as many times as necessary in every direction, starting from the point
 * (0,0,0).
 */
interface RepeatedDiggingPattern : DiggingPattern {

    /**
     * The width of this pattern. This dimension is related to the X coordinate.
     */
    val width: Int

    /**
     * The height of this pattern. This dimension is related to the Y coordinate.
     */
    val height: Int

    /**
     * The length of this pattern. This dimension is related to the Z coordinate.
     */
    val length: Int

    /**
     * Gives the coordinates where the player has to enter a sample to start digging this pattern. Multiple accesses may
     * be returned, meaning the player has to enter each of them independently to dig this pattern.
     *
     * The accesses' positions in the sample depend on the position of the pattern within the sample.
     *
     * @param offsetX the X position of this pattern within the sample
     * @param offsetY the Y position of this pattern within the sample
     * @return the set of accesses at the given pattern position
     */
    fun getAccesses(offsetX: Int, offsetY: Int): Set<Access>

    /**
     * Digs this pattern into the given [sample] with the given offset. This method must take care of stopping at the
     * edge of the given sample.
     */
    fun digInto(sample: Sample, offsetX: Int = 0, offsetY: Int = 0, offsetZ: Int = 0)

    override fun digInto(sample: Sample) {
        val accesses = mutableListOf<Access>()
        for (x in 0 until sample.dimensions.width step width) {
            for (y in 0 until sample.dimensions.height step height) {
                accesses.addAll(getAccesses(x, y))
                for (z in 0 until sample.dimensions.length step length) {
                    digInto(sample, x, y, z)
                }
            }
        }
    }
}
