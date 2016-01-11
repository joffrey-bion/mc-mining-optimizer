package org.hildan.minecraft.mining.optimizer.chunks;

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
    public Block(int x, int y, int z) {
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
    public Block(Block source) {
        super(source);
        this.type = source.type;
        this.headAccessible = source.headAccessible;
        this.visible = source.visible;
    }

    public BlockType getType() {
        return type;
    }

    void setType(BlockType type) {
        this.type = type;
    }

    public boolean isHeadAccessible() {
        return headAccessible;
    }

    void setHeadAccessible(boolean headAccessible) {
        this.headAccessible = headAccessible;
    }

    public boolean isFeetAccessible() {
        return feetAccessible;
    }

    public void setFeetAccessible(boolean feetAccessible) {
        this.feetAccessible = feetAccessible;
    }

    public boolean isVisible() {
        return visible;
    }

    void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isExplored() {
        return explored;
    }

    public void setExplored(boolean explored) {
        this.explored = explored;
    }

    public boolean isDug() {
        return type == BlockType.AIR;
    }

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
        return getX() == block.getX() && getY() == block.getY() && getZ() == block.getZ();
    }

    @Override
    public int hashCode() {
        int result = getX();
        result = 31 * result + getY();
        result = 31 * result + getZ();
        return result;
    }

    @Override
    public String toString() {
        return type.toString();
    }
}
