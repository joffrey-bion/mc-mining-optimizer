package org.hildan.minecraft.mining.optimizer.patterns

import org.hildan.minecraft.mining.optimizer.blocks.explore
import org.hildan.minecraft.mining.optimizer.blocks.Sample
import org.hildan.minecraft.mining.optimizer.geometry.Dimensions

/**
 * Represents a way to dig into the stone in 3 dimensions.
 */
interface DiggingPattern {

    /**
     * Gives the coordinates where the player has to enter a sample to start digging this pattern. Multiple accesses may
     * be returned, meaning the player has to enter each of them independently to dig this pattern. The returned
     * accesses must all be within the given dimensions.
     */
    fun getAccesses(dimensions: Dimensions): Set<Access>

    /**
     * Digs this pattern into the given [sample]. This method must take care of stopping at the edge of the given sample.
     */
    fun digInto(sample: Sample)

    /**
     * Digs this pattern into the given [sample], and then digs every visible ore recursively until no more ore is
     * directly visible. This is supposed to represent what a real user will do while digging the pattern.
     */
    fun digAndFollowOres(sample: Sample) {
        digInto(sample)
        val accesses = getAccesses(sample.dimensions)
        sample.explore(accesses)
        sample.digVisibleOres()
    }
}
