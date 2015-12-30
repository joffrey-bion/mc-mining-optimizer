package org.hildan.minecraft.mining.optimizer.patterns;

import org.hildan.minecraft.mining.optimizer.chunks.DugChunk;
import org.hildan.minecraft.mining.optimizer.chunks.OredChunk;

public interface Pattern {

    /**
     * Gets the width of this chunk. This dimension is related to the X coordinate.
     *
     * @return the width of this chunk
     */
    int getWidth();

    /**
     * Gets the height of this chunk. This dimension is related to the Y coordinate.
     *
     * @return the height of this chunk
     */
    int getHeight();

    /**
     * Gets the length of this chunk. This dimension is related to the Z coordinate.
     *
     * @return the length of this chunk
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
    default DugChunk dig(OredChunk oredChunk) {
        DugChunk dugChunk = new DugChunk(oredChunk);
        for (int x = 0; x < dugChunk.getWidth(); x += getWidth()) {
            for (int y = 0; y < dugChunk.getHeight(); y += getHeight()) {
                for (int z = 0; z < dugChunk.getLength(); z += getLength()) {
                    digInto(dugChunk, x, y, z);
                }
            }
        }
        return dugChunk;
    }

    /**
     * Digs this pattern into the given chunk, starting from the given origin, and going in the increasing direction of each coordinate.
     * This method takes care of stopping at the edge of the given chunk.
     */
    void digInto(DugChunk chunk, int originX, int originY, int originZ);

    /**
     * Returns whether it is actually possible to dig this pattern in the game.
     *
     * @return true if it is actually possible to dig this pattern in the game.
     */
    default boolean isValid() {
        DugChunk dugChunk = new DugChunk(getWidth(), getHeight(), getLength());
        return dugChunk.isValid(getAccesses());
    }
}
