package org.hildan.minecraft.mining.optimizer.blocks;

import org.hildan.minecraft.mining.optimizer.geometry.Position;

/**
 * Represents a Minecraft block.
 */
public class Block extends Position {

    private BlockType type;

    private boolean headAccessible;

    private boolean feetAccessible;

    private boolean visible;

    private boolean explored;

    /**
     * Creates a new stone block at the given position.
     */
    Block(int x, int y, int z) {
        super(x, y, z);
        this.type = BlockType.STONE;

        // exploration does not get to every block, hence the need to initialize to false
        this.headAccessible = false;
        this.feetAccessible = false;
        this.visible = false;
        this.explored = false;
    }

    /**
     * Creates a copy of the given block.
     *
     * @param source
     *         the block to copy
     */
    Block(Block source) {
        super(source);
        this.type = source.type;
        this.headAccessible = source.headAccessible;
        this.feetAccessible = source.feetAccessible;
        this.visible = source.visible;
        this.explored = source.explored;
    }

    /**
     * Gets the type of this block.
     *
     * @return the type of this block.
     */
    public BlockType getType() {
        return type;
    }

    /**
     * Sets the type of this block.
     *
     * @param type
     *         the new type for this block
     */
    void setType(BlockType type) {
        this.type = type;
    }

    boolean isHeadAccessible() {
        return headAccessible;
    }

    void setHeadAccessible(boolean headAccessible) {
        this.headAccessible = headAccessible;
    }

    boolean isFeetAccessible() {
        return feetAccessible;
    }

    void setFeetAccessible(boolean feetAccessible) {
        this.feetAccessible = feetAccessible;
    }

    public boolean isVisible() {
        return visible;
    }

    void setVisible(boolean visible) {
        this.visible = visible;
    }

    boolean isExplored() {
        return explored;
    }

    void setExplored(boolean explored) {
        this.explored = explored;
    }

    /**
     * Returns whether this block has been dug.
     *
     * @return true if this block has been dug.
     */
    public boolean isDug() {
        return type == BlockType.AIR;
    }

    /**
     * Returns whether this block is an ore block.
     *
     * @return true if this block is an ore block.
     */
    public boolean isOre() {
        return type.isOre();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Block block = (Block) obj;
        return getX() == block.getX() && getY() == block.getY() && getZ() == block.getZ() && type == block.type;
    }

    @Override
    public int hashCode() {
        int result = getX();
        result = 31 * result + getY();
        result = 31 * result + getZ();
        result = 31 * result + type.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return type.toString();
    }
}
