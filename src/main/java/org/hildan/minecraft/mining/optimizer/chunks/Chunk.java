package org.hildan.minecraft.mining.optimizer.chunks;

import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern;

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

    /**
     * Returns whether this chunk can indeed be dug like this in game.
     *
     * @param accesses
     *     the coordinates where the player enters this chunk to start digging, as specified by {@link DiggingPattern#getAccesses()}
     *
     * @return true if this chunk can indeed be dug like this in game.
     */
    public boolean isValid(int[][] accesses) {

        // TODO check whether the dug blocks are arranged in a way that could indeed have been dug

        return false;
    }

    private int getIndex(int x, int y, int z) {
        int index = x + y * width + z * width * height;
        if (index < 0 || index >= blocks.length) {
            throw new IllegalArgumentException(String.format("The given coordinates (%d,%d,%d) are out of bounds for this chunk", x, y, z));
        }
        return index;
    }

    private static int[] getAdjacentIndexes(int i, int size) {
        if (i == 0) {
            return new int[] { size - 1, i, i + 1 }; // wrapped max
        }
        if (i == size - 1) {
            return new int[] { i - 1, i, 0 }; // wrapped min
        }
        return new int[] { i - 1, i, i + 1 };
    }

    /**
     * Returns whether the given coordinates belong to this chunk.
     *
     * @param x
     *     the X coordinate to test
     * @param y
     *     the Y coordinate to test
     * @param z
     *     the Z coordinate to test
     *
     * @return true if the given coordinates belong to this chunk.
     */
    public boolean hasBlock(int x, int y, int z) {
        return x < width && y < height && z < length;
    }

    /**
     * Sets the content of the given block.
     *
     * @param x
     *     the X coordinate of the block to set
     * @param y
     *     the Y coordinate of the block to set
     * @param z
     *     the Z coordinate of the block to set
     */
    public void setBlock(int x, int y, int z, Block block) {
        blocks[getIndex(x, y, z)] = block;
    }

    /**
     * Gets the content of the given block.
     *
     * @param x
     *     the X coordinate of the block to get
     * @param y
     *     the Y coordinate of the block to get
     * @param z
     *     the Z coordinate of the block to get
     *
     * @return the Block located at the provided coordinates
     */
    public Block getBlock(int x, int y, int z) {
        return blocks[getIndex(x, y, z)];
    }

    private long getCountOfBlocksMatching(Predicate<Block> predicate) {
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

    public long getOresCount() {
        return getCountOfBlocksMatching(Block::isOre);
    }

    public long getDugBlocksCount() {
        return getCountOfBlocksMatching(b -> b == Block.AIR);
    }

    public void dig(int x, int y, int z) {
        setBlock(x, y, z, Block.AIR);
    }

    public boolean isDug(int x, int y, int z) {
        return getBlock(x, y, z) == Block.AIR;
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

        // TODO add actual visibility algorithm from possible standing positions

        return isDug(x, y, z) || hasDugNeighbor(x, y, z);
    }
}
