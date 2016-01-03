package org.hildan.minecraft.mining.optimizer.chunks;

/**
 * Defines how some functions behave when reaching the side of the sample.
 */
public enum Wrapping {

    /**
     * Does not consider blocks that are outside the sample.
     */
    CUT,
    /**
     * Wraps to the other side of the sample. This means, when reaching for instance a Y value beyond the height of the
     * sample, it wraps back to Y=0.
     */
    WRAP
}
