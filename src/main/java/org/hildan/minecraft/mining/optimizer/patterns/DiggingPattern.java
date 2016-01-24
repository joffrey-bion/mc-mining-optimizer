package org.hildan.minecraft.mining.optimizer.patterns;

import org.hildan.minecraft.mining.optimizer.chunks.Sample;

import java.util.Set;

/**
 * Represents a way to dig into the stone in 3 dimensions.
 */
public interface DiggingPattern {

    /**
     * Gets the width of this pattern. This dimension is related to the X coordinate.
     *
     * @return the width of this pattern
     */
    int getWidth();

    /**
     * Gets the height of this pattern. This dimension is related to the Y coordinate.
     *
     * @return the height of this pattern
     */
    int getHeight();

    /**
     * Gets the length of this pattern. This dimension is related to the Z coordinate.
     *
     * @return the length of this pattern
     */
    int getLength();

    /**
     * Gives the coordinates where the player has to enter a sample to start digging this pattern. Multiple accesses may
     * be returned, meaning the player has to enter each of them independently to dig this pattern.
     * <p>
     * The accesses' positions in the sample depend on the position of the pattern within the sample.
     *
     * @param x
     *         the X position of this pattern within the sample
     * @param y
     *         the Y position of this pattern within the sample
     * @return the set of accesses at the given pattern position
     */
    Set<Access> getAccesses(int x, int y);

    /**
     * Digs this pattern into the given sample. The pattern is repeated as many times as necessary in every direction,
     * starting from the point (0,0,0).
     */
    void digInto(Sample sample);
}
