package org.hildan.minecraft.mining.optimizer.chunks;

import org.hildan.minecraft.mining.optimizer.geometry.Position;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * An arbitrary group of blocks. It can have any dimension, thus it is different from a minecraft chunk, which is
 * 16x256x16.
 */
public class Sample {

    /**
     * The width of a standard Minecraft chunk.
     */
    public static final int CHUNK_WIDTH = 16;

    /**
     * The length of a standard Minecraft chunk.
     */
    public static final int CHUNK_LENGTH = 16;

    private final int width;

    private final int height;

    private final int length;

    private final Block[] blocks;

    /**
     * Creates a new pure stone chunk of the given dimensions.
     */
    public Sample(int width, int height, int length) {
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
     * Creates a copy of the given Sample.
     *
     * @param source
     *         the Sample to copy
     */
    public Sample(Sample source) {
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

    private int getIndex(int x, int y, int z) throws NoSuchElementException {
        if (!hasBlock(x, y, z)) {
            throw new NoSuchElementException(String.format("Block (%d,%d,%d) does not exist in this chunk", x, y, z));
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
        return blocks[getIndex(x, y, z)];
    }

    /**
     * Gets the Block located at the given absolute position.
     *
     * @param position
     *         the absolute position of the block to get
     * @return the Block located at the provided coordinates
     */
    public Block getBlock(Position position) throws NoSuchElementException {
        return getBlock(position.getX(), position.getY(), position.getZ());
    }

    /**
     * Gets the Block located at the given relative position.
     *
     * @param origin
     *         the position to start from
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
    public Block getBlock(Position origin, int distanceX, int distanceY, int distanceZ, Wrapping wrapping) {
        int x = 0;
        int y = 0;
        int z = 0;
        switch (wrapping) {
            case CUT:
                x = origin.getX() + distanceX;
                y = origin.getY() + distanceY;
                z = origin.getZ() + distanceZ;
                if (!hasBlock(x, y, z)) {
                    return null;
                }
                break;
            case WRAP:
                x = Math.floorMod(origin.getX() + distanceX, width);
                y = Math.floorMod(origin.getY() + distanceY, height);
                z = Math.floorMod(origin.getZ() + distanceZ, length);
                break;
        }
        return getBlock(x, y, z);
    }

    /**
     * Gets the block above the given position.
     *
     * @param position
     *         the position above which to get a block
     * @param wrapping
     *         the wrapping policy when the given block is the ceiling of this sample
     * @return the above block, or null if the given block is the ceiling of this sample and wrapping is set to {@link
     * Wrapping#CUT}
     */
    public Block getBlockAbove(Position position, Wrapping wrapping) {
        return getBlock(position, 0, 1, 0, wrapping);
    }

    /**
     * Gets the block below the given one.
     *
     * @param position
     *         the position below which to get a block
     * @param wrapping
     *         the wrapping policy when the given block is the floor of this sample
     * @return the above block, or null if the given block is the floor of this sample and wrapping is set to {@link
     * Wrapping#CUT}
     */
    public Block getBlockBelow(Position position, Wrapping wrapping) {
        return getBlock(position, 0, -1, 0, wrapping);
    }

    /**
     * Gets the 6 blocks that are adjacent to given position. If wrapping is set to {@link Wrapping#CUT} and the given
     * position is on this chunk's side, less than 6 blocks are returned because part of them is cut off.
     *
     * @param position
     *         the position to get the neighbors from
     * @param wrapping
     *         the wrapping policy when the given block is on the side of this chunk
     * @return a list containing blocks adjacent to the given position
     */
    public Collection<Block> getAdjacentBlocks(Position position, Wrapping wrapping) {
        Collection<Block> adjacentBlocks = new HashSet<>(6);
        adjacentBlocks.add(getBlock(position, +1, 0, 0, wrapping));
        adjacentBlocks.add(getBlock(position, -1, 0, 0, wrapping));
        adjacentBlocks.add(getBlock(position, 0, +1, 0, wrapping));
        adjacentBlocks.add(getBlock(position, 0, -1, 0, wrapping));
        adjacentBlocks.add(getBlock(position, 0, 0, +1, wrapping));
        adjacentBlocks.add(getBlock(position, 0, 0, -1, wrapping));
        adjacentBlocks.removeIf(b -> b == null);
        return adjacentBlocks;
    }

    public Iterable<Block> getBlocksMatching(Predicate<Block> predicate) {
        Collection<Block> matchingBlocks = new HashSet<>();
        for (Block block : blocks) {
            if (predicate.test(block)) {
                matchingBlocks.add(block);
            }
        }
        return matchingBlocks;
    }

    private long getNumberOfBlocksMatching(Predicate<Block> predicate) {
        long count = 0L;
        for (Block block : blocks) {
            if (predicate.test(block)) {
                count++;
            }
        }
        return count;
    }

    public long getOresCount() {
        return getNumberOfBlocksMatching(Block::isOre);
    }

    public long getDugBlocksCount() {
        return getNumberOfBlocksMatching(Block::isDug);
    }

    public void dig(Position position) {
        dig(position.getX(), position.getY(), position.getZ());
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
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Sample sample = (Sample) obj;

        if (width != sample.width) {
            return false;
        }
        if (height != sample.height) {
            return false;
        }
        if (length != sample.length) {
            return false;
        }
        return Arrays.equals(blocks, sample.blocks);
    }

    @Override
    public int hashCode() {
        int result = width;
        result = 31 * result + height;
        result = 31 * result + length;
        result = 31 * result + Arrays.hashCode(blocks);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(String.format("Size: %d %d %d%n%n", width, height, length));
        final String layerSeparator = "  ";

        final String columnTitleFormat = layerSeparator + '%' + width + 's';
        sb.append(' ');
        for (int y = 0; y < height; y++) {
            sb.append(String.format(columnTitleFormat, String.format("Y = %d", y)));
        }
        sb.append(String.format("%n"));

        for (int z = 0; z < length; z++) {
            sb.append(String.format("%2d ", z));
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    sb.append(getBlock(x, y, z));
                }
                sb.append(layerSeparator);
            }
            sb.append(String.format("%n"));
        }
        return sb.toString();
    }
}
