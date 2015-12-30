package org.hildan.minecraft.mining.optimizer.chunks;

import org.hildan.minecraft.mining.optimizer.patterns.Pattern;

/**
 * An OredChunk that can be dug.
 */
public class DugChunk extends OredChunk {

    /**
     * Creates a new pure stone chunk of the given dimensions.
     */
    public DugChunk(int width, int height, int length) {
        super(width, height, length);
    }

    /**
     * Creates a copy of the given Chunk.
     *
     * @param source
     *     the Chunk to copy
     */
    public DugChunk(Chunk source) {
        super(source);
    }

    public void dig(int x, int y, int z) {
        setBlock(x, y, z, Block.AIR);
    }

    public boolean isDug(int x, int y, int z) {
        return getBlock(x, y, z) == Block.AIR;
    }

    protected static int[] getAdjacentIndexes(int i, int size) {
        if (i == 0) {
            return new int[] { size - 1, i, i + 1 }; // wrapped max
        }
        if (i == size - 1) {
            return new int[] { i - 1, i, 0 }; // wrapped min
        }
        return new int[] { i - 1, i, i + 1 };
    }

    public boolean hasDugNeighbor(int x, int y, int z) {
        int[] adjXs = getAdjacentIndexes(x, getWidth());
        int[] adjYs = getAdjacentIndexes(y, getHeight());
        int[] adjZs = getAdjacentIndexes(z, getLength());
        for (int adjX : adjXs) {
            for (int adjY : adjYs) {
                for (int adjZ : adjZs) {
                    if (x == adjX && y == adjY && z == adjZ) {
                        // skip self
                        continue;
                    }
                    if (isDug(adjX, adjY, adjZ)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isDiscovered(int x, int y, int z) {
        // this is sufficient if this chunk is valid because one can always see
        return isDug(x, y, z) || hasDugNeighbor(x, y, z);
    }

    /**
     * Returns whether this chunk can indeed be dug like this in game.
     *
     * @param accesses
     *     the coordinates where the player enters this chunk to start digging, as specified by {@link Pattern#getAccesses()}
     *
     * @return true if this chunk can indeed be dug like this in game.
     */
    public boolean isValid(int[][] accesses) {
        // TODO check whether the dug blocks are arranged in a way that could indeed have been dug
        return false;
    }

    public long getNumberOfDugBlocks() {
        return countBlocksMatching(b -> b == Block.AIR);
    }
}
