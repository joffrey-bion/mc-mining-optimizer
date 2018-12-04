package org.hildan.minecraft.mining.optimizer.patterns

import org.hildan.minecraft.mining.optimizer.blocks.Sample

/**
 * Represents a way to dig into the stone in 3 dimensions.
 */
interface DiggingPattern {
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
        sample.digVisibleOresRecursively()
    }
}
