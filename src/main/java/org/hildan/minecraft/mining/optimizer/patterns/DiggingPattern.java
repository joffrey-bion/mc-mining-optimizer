package org.hildan.minecraft.mining.optimizer.patterns;

import org.hildan.minecraft.mining.optimizer.chunks.Chunk;

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
     * Gives the coordinates where the player has to enter a chunk to start digging this pattern. Each set of coordinates fully defines a
     * single access: the given block is the lower block occupied by the body of the player, and the upper block (occupied by the head of
     * the player) is inferred from it. If multiple points are given, it means the player has to enter each of them independently.
     *
     * @return an array (of unspecified length) of arrays of coordinates (each of length 3)
     */
    int[][] getAccesses();

    /**
     * Digs this pattern into the given oredChunk to produce a new DugChunk. The pattern is repeated as many times as necessary in every
     * direction, starting from the point (0,0,0).
     */
    Chunk dig(Chunk oredChunk);

    /**
     * Returns whether it is actually possible to dig this pattern in the game.
     *
     * @return true if it is actually possible to dig this pattern in the game.
     */
    default boolean isValid() {
        Chunk chunk = new Chunk(getWidth(), getHeight(), getLength());
        return chunk.isValid(getAccesses());
    }
}
