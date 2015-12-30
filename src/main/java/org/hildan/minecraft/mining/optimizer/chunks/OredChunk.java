package org.hildan.minecraft.mining.optimizer.chunks;

/**
 * A chunk of blocks where blocks can be ores.
 */
public class OredChunk extends Chunk {

    /**
     * Creates a new pure stone chunk of the given dimensions.
     */
    public OredChunk(int width, int height, int length) {
        super(width, height, length);
    }

    /**
     * Creates a copy of the given Chunk.
     *
     * @param source the Chunk to copy
     */
    public OredChunk(Chunk source) {
        super(source);
    }

    public void setOre(int x, int y, int z) {
        setBlock(x, y, z, Block.ORE);
    }

    public boolean isOre(int x, int y, int z) {
        return getBlock(x, y, z) == Block.ORE;
    }

    public long getNumberOfOres() {
        return countBlocksMatching(b -> b == Block.ORE);
    }
}
