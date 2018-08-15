package org.hildan.minecraft.mining.optimizer.patterns

import org.hildan.minecraft.mining.optimizer.blocks.Sample

/**
 * Represents a way to dig into the stone in 3 dimensions.
 */
interface DiggingPattern {

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
     * @param originX the X position of this pattern within the sample
     * @param originY the Y position of this pattern within the sample
     * @return the set of accesses at the given pattern position
     */
    fun getAccesses(originX: Int, originY: Int): Set<Access>

    /**
     * Digs this pattern into the given sample. The pattern is repeated as many times as necessary in every direction,
     * starting from the point (0,0,0).
     */
    fun digInto(sample: Sample)
}
