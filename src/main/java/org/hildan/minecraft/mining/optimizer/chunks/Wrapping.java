package org.hildan.minecraft.mining.optimizer.chunks;

/**
 * Defines how some functions behave when reaching the side of the chunk.
 */
public enum Wrapping {

    /**
     * Does not consider blocks that are outside the chunk.
     */
    CUT,
    /**
     * Wraps to the other side of the chunk. This means, when reaching for instance a Y value beyond the height of the
     * chunk, it wraps back to Y=0.
     */
    WRAP
}
