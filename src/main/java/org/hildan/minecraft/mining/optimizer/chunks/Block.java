package org.hildan.minecraft.mining.optimizer.chunks;

/**
 * Represents a Minecraft block.
 */
public class Block {

    private final int x;

    private final int y;

    private final int z;

    private BlockType type;

    private boolean headAccessible;

    private boolean visible;

    /**
     * Creates a new stone block at the given position.
     */
    public Block(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = BlockType.STONE;

        // exploration does not get to every block, hence the need to initialize to false
        this.headAccessible = false;
        this.visible = false;
    }

    /**
     * Creates a copy of the given block.
     *
     * @param source
     *         the block to copy
     */
    public Block(Block source) {
        this.x = source.x;
        this.y = source.y;
        this.z = source.z;
        this.type = source.type;
        this.headAccessible = source.headAccessible;
        this.visible = source.visible;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
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

    public boolean isVisible() {
        return visible;
    }

    void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isDug() {
        return type == BlockType.AIR;
    }

    public boolean isOre() {
        return type.isOre();
    }

    public String toString() {
        return type.toString();
    }
}
