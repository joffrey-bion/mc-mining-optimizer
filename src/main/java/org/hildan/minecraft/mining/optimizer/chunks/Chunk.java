package org.hildan.minecraft.mining.optimizer.chunks;

import java.util.Arrays;
import java.util.function.Predicate;

/**
 * A chunk of blocks.
 */
public class Chunk {

    private final int width;

    private final int height;

    private final int length;

    private final Block[] blocks;

    /**
     * Creates a new pure stone chunk of the given dimensions.
     */
    public Chunk(int width, int height, int length) {
        this.width = width;
        this.height = height;
        this.length = length;
        this.blocks = new Block[this.width * this.height * this.length];
        // initialize with full blocks
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                for (int z = 0; z < this.length; z++) {
                    setBlock(x, y, z, Block.STONE);
                }
            }
        }
    }

    /**
     * Creates a copy of the given Chunk.
     *
     * @param source
     *     the Chunk to copy
     */
    public Chunk(Chunk source) {
        this.width = source.width;
        this.height = source.height;
        this.length = source.length;
        this.blocks = Arrays.copyOf(source.blocks, source.blocks.length);
    }

    /**
     * Gets the width of this chunk. This dimension is related to the X coordinate.
     *
     * @return the width of this chunk
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the height of this chunk. This dimension is related to the Y coordinate.
     *
     * @return the height of this chunk
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the length of this chunk. This dimension is related to the Z coordinate.
     *
     * @return the length of this chunk
     */
    public int getLength() {
        return length;
    }

    private int getIndex(int x, int y, int z) {
        int index = x + y * width + z * width * height;
        if (index < 0 || index >= blocks.length) {
            throw new IllegalArgumentException(String.format("The given coordinates (%d,%d,%d) are out of bounds for this chunk", x, y, z));
        }
        return index;
    }

    protected void setBlock(int x, int y, int z, Block block) {
        blocks[getIndex(x, y, z)] = block;
    }

    protected Block getBlock(int x, int y, int z) {
        return blocks[getIndex(x, y, z)];
    }

    protected long countBlocksMatching(Predicate<Block> predicate) {
        long count = 0;
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                for (int z = 0; z < this.length; z++) {
                    if (predicate.test(getBlock(x, y, z))) {
                        count++;
                    }
                }
            }
        }
        return count;
    }
}
