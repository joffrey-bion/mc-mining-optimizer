package org.hildan.minecraft.mining.optimizer.blocks;

import org.hildan.minecraft.mining.optimizer.geometry.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * An arbitrary group of blocks. It can have any dimension, thus it is different from a minecraft chunk, which is
 * 16x256x16.
 */
public class Sample {

    private final int width;

    private final int height;

    private final int length;

    private final Block[] blocks;

    private int oreBlocksCount = 0;

    private int dugBlocksCount = 0;

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
        this.oreBlocksCount = source.oreBlocksCount;
        this.dugBlocksCount = source.dugBlocksCount;
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

    /**
     * Returns the index in the internal array of the block at the given position.
     *
     * @param x
     *         the X coordinate of the block to get the index of
     * @param y
     *         the Y coordinate of the block to get the index of
     * @param z
     *         the Z coordinate of the block to get the index of
     * @return the index of the given block in teh internal array {@link #blocks}
     * @throws NoSuchElementException
     *         if the given coordinates are out of bound
     */
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
     * @throws NoSuchElementException
     *         if the given coordinates are out of bound
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
     * @throws NoSuchElementException
     *         if the given coordinates are out of bound
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
     * Gets the 4 blocks that are horizontally adjacent to given position. If wrapping is set to {@link Wrapping#CUT}
     * and the given position is on this chunk's side, less than 4 blocks are returned because part of them is cut off.
     *
     * @param position
     *         the position to get the neighbors from
     * @param wrapping
     *         the wrapping policy when the given block is on the side of this chunk
     * @return a list containing blocks adjacent to the given position
     */
    public Collection<Block> getHorizontallyAdjacentBlocks(Position position, Wrapping wrapping) {
        Collection<Block> adjacentBlocks = new ArrayList<>(4);
        adjacentBlocks.add(getBlock(position, +1, 0, 0, wrapping));
        adjacentBlocks.add(getBlock(position, -1, 0, 0, wrapping));
        adjacentBlocks.add(getBlock(position, 0, 0, +1, wrapping));
        adjacentBlocks.add(getBlock(position, 0, 0, -1, wrapping));
        if (wrapping == Wrapping.CUT) {
            adjacentBlocks.removeIf(b -> b == null);
        }
        return adjacentBlocks;
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
        Collection<Block> adjacentBlocks = new ArrayList<>(6);
        adjacentBlocks.add(getBlock(position, +1, 0, 0, wrapping));
        adjacentBlocks.add(getBlock(position, -1, 0, 0, wrapping));
        adjacentBlocks.add(getBlock(position, 0, +1, 0, wrapping));
        adjacentBlocks.add(getBlock(position, 0, -1, 0, wrapping));
        adjacentBlocks.add(getBlock(position, 0, 0, +1, wrapping));
        adjacentBlocks.add(getBlock(position, 0, 0, -1, wrapping));
        if (wrapping == Wrapping.CUT) {
            adjacentBlocks.removeIf(b -> b == null);
        }
        return adjacentBlocks;
    }

    /**
     * Returns the collection of all blocks matching the given predicate in this sample.
     *
     * @param predicate
     *         the predicate to test the blocks
     * @return the collection of all blocks matching the given predicate in this sample.
     */
    public Iterable<Block> getBlocksMatching(Predicate<Block> predicate) {
        return Arrays.stream(blocks).filter(predicate).collect(Collectors.toList());
    }

    /**
     * Returns the number of dug blocks.
     *
     * @return the number of dug blocks.
     */
    public int getDugBlocksCount() {
        return dugBlocksCount;
    }

    /**
     * Returns the number of ore blocks.
     *
     * @return the number of ore blocks.
     */
    public int getOreBlocksCount() {
        return oreBlocksCount;
    }

    /**
     * Changes the type of the block at the given position.
     *
     * @param x
     *         the X coordinate of the block to change
     * @param y
     *         the Y coordinate of the block to change
     * @param z
     *         the Z coordinate of the block to change
     * @param type
     *         the new type of the block
     */
    public void setBlock(int x, int y, int z, BlockType type) {
        Block block = getBlock(x, y, z);
        BlockType formerType = block.getType();
        block.setType(type);
        if (!formerType.isOre() && type.isOre()) {
            oreBlocksCount++;
        } else if (formerType.isOre() && !type.isOre()) {
            oreBlocksCount--;
        }
        if (formerType != BlockType.AIR && type == BlockType.AIR) {
            dugBlocksCount++;
        } else if (formerType == BlockType.AIR && type != BlockType.AIR) {
            dugBlocksCount--;
        }
    }

    /**
     * Digs the block at the specified position.
     *
     * @param position
     *         the position to dig at
     */
    public void digBlock(Position position) {
        digBlock(position.getX(), position.getY(), position.getZ());
    }

    /**
     * Digs the block at the specified position.
     *
     * @param x
     *         the X coordinate of the block to dig
     * @param y
     *         the Y coordinate of the block to dig
     * @param z
     *         the Z coordinate of the block to dig
     */
    public void digBlock(int x, int y, int z) {
        setBlock(x, y, z, BlockType.AIR);

        // TODO move visibility logic to external visitor
        Block block = getBlock(x, y, z);
        block.setVisible(true);
        getAdjacentBlocks(block, Wrapping.WRAP).forEach(b -> b.setVisible(true));
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
        return String.format("Size: %d %d %d  Dug: %d", width, height, length, dugBlocksCount);
    }

    public String toFullString() {
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
