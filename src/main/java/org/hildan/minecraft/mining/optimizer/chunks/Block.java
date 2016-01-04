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

    private boolean feetAccessible;

    private boolean visible;

    private boolean explored;

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
        return x == block.x && y == block.y && z == block.z;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + z;
        return result;
    }

    public String toString() {
        return type.toString();
    }
}
