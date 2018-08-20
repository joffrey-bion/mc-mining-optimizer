package org.hildan.minecraft.mining.optimizer.blocks

/**
 * Defines how some functions behave when reaching the side of the sample.
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
