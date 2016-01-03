package org.hildan.minecraft.mining.optimizer.chunks;

import org.hildan.minecraft.mining.optimizer.geometry.Position;
import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * A group of blocks.
 */
public class Chunk {

    public static final int MC_CHUNK_WIDTH = 16;

    public static final int MC_CHUNK_LENGTH = 16;

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
        // initialize with stone blocks
        for (int z = 0; z < this.length; z++) {
            for (int y = 0; y < this.height; y++) {
                for (int x = 0; x < this.width; x++) {
                    blocks[getIndex(x, y, z)] = new Block(x, y, z);
                }
            }
        }
    }

    /**
     * Creates a copy of the given Chunk.
     *
     * @param source
     *         the Chunk to copy
     */
    public Chunk(Chunk source) {
        this.width = source.width;
        this.height = source.height;
        this.length = source.length;
        this.blocks = new Block[source.blocks.length];
        for (int i = 0; i < blocks.length; i++) {
            blocks[i] = new Block(source.blocks[i]);
        }
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
     *         the coordinates where the player enters this chunk to start digging, as specified by {@link
     *         DiggingPattern#getAccesses()}
     * @return true if this chunk can indeed be dug like this in game.
     */
    public boolean isValid(List<Position> accesses) {

        // TODO check whether the dug blocks are arranged in a way that could indeed have been dug

        return false;
    }

    /**
     * Returns whether the given coordinates belong to this chunk.
     *
     * @param x
     *         the X coordinate to test
     * @param y
     *         the Y coordinate to test
     * @param z
     *         the Z coordinate to test
     * @return true if the given coordinates belong to this chunk.
     */
    public boolean hasBlock(int x, int y, int z) {
        return x >= 0 && y >= 0 && z >= 0 && x < width && y < height && z < length;
    }

    private int getIndex(int x, int y, int z) {
        if (!hasBlock(x, y, z)) {
            throw new IllegalArgumentException(
                    String.format("The given coordinates (%d,%d,%d) are out of bounds for this chunk", x, y, z));
        }
        return x + y * width + z * width * height;
    }

    /**
     * Gets the Block located at the given absolute position.
     *
     * @param x
     *         the X coordinate of the block to get
     * @param y
     *         the Y coordinate of the block to get
     * @param z
     *         the Z coordinate of the block to get
     * @return the Block located at the provided coordinates
     */
    public Block getBlock(int x, int y, int z) throws NoSuchElementException {
        try {
            return blocks[getIndex(x, y, z)];
        } catch (IllegalArgumentException e) {
            throw new NoSuchElementException(String.format("Block (%d,%d,%d) does not exist in this chunk", x, y, z));
        }
    }

    /**
     * Gets the Block located at the given relative position.
     *
     * @param origin
     *         the block to start from
     * @param distanceX
     *         the distance to travel in the X direction (may be negative)
     * @param distanceY
     *         the distance to travel in the Y direction (may be negative)
     * @param distanceZ
     *         the distance to travel in the Z direction (may be negative)
     * @param wrapping
     *         defines the decision to take when reaching the end of the chunk
     * @return the Block located at the given position, or null if the block is out of bounds and wrapping is set to
     * {@link Wrapping#CUT}.
     */
    public Block getBlock(Block origin, int distanceX, int distanceY, int distanceZ, Wrapping wrapping) {
        try {
            int x = 0, y = 0, z = 0;
            switch (wrapping) {
                case CUT:
                    x = origin.getX() + distanceX;
                    y = origin.getY() + distanceY;
                    z = origin.getZ() + distanceZ;
                    break;
                case WRAP:
                    x = Math.floorMod(origin.getX() + distanceX, width);
                    y = Math.floorMod(origin.getY() + distanceY, height);
                    z = Math.floorMod(origin.getZ() + distanceZ, length);
                    break;
            }
            return getBlock(x, y, z);
        } catch (NoSuchElementException e) {
            return null; // if going out of bounds with Wrapping.CUT
        }
    }

    /**
     * Gets the 6 blocks that are adjacent to given one. If wrapping is set to {@link Wrapping#CUT} and the given block
     * is on this chunk's side, less than 6 blocks are returned because part of them is cut off.
     *
     * @param block
     *         the block to get the neighbors from
     * @param wrapping
     *         the wrapping policy when the given block is on the side of this chunk
     * @return a list containing blocks adjacent to the given block
     */
    public List<Block> getAdjacentBlocks(Block block, Wrapping wrapping) {
        List<Block> adjacentBlocks = new ArrayList<>(6);
        adjacentBlocks.add(getBlock(block, +1, 0, 0, wrapping));
        adjacentBlocks.add(getBlock(block, -1, 0, 0, wrapping));
        adjacentBlocks.add(getBlock(block, 0, +1, 0, wrapping));
        adjacentBlocks.add(getBlock(block, 0, -1, 0, wrapping));
        adjacentBlocks.add(getBlock(block, 0, 0, +1, wrapping));
        adjacentBlocks.add(getBlock(block, 0, 0, -1, wrapping));
        adjacentBlocks.removeIf(b -> b == null);
        return adjacentBlocks;
    }

    private long getCountOfBlocksMatching(Predicate<Block> predicate) {
        long count = 0L;
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
        return getCountOfBlocksMatching(Block::isDug);
    }

    public void dig(int x, int y, int z) {
        Block block = getBlock(x, y, z);
        block.setType(BlockType.AIR);

        // TODO move visibility logic to external visitor
        block.setVisible(true);
        getAdjacentBlocks(block, Wrapping.WRAP).forEach(b -> b.setVisible(true));
    }

    public void putOre(int x, int y, int z, BlockType type) {
        assert type.isOre() : "the provided type has to be an Ore type";
        getBlock(x, y, z).setType(type);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(String.format("Size: %d %d %d%n%n", width, height, length));
        String columnTitleFormat = "%" + width + "s ";
        for (int y = 0; y < height; y++) {
            sb.append(String.format(columnTitleFormat, String.format("y = %d", y)));
        }
        sb.append(String.format("%n"));
        for (int z = 0; z < length; z++) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    sb.append(getBlock(x, y, z));
                }
                sb.append(' ');
            }
            sb.append(String.format("%n"));
        }
        return sb.toString();
    }
}
