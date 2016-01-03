package org.hildan.minecraft.mining.optimizer.chunks;

/**
 * Represents the content of a Minecraft block.
 */
public enum BlockType {

    AIR(false, "\u2592"),
    STONE(false, "\u2588"),
    COAL_ORE(true, "c"),
    IRON_ORE(true, "i"),
    GOLD_ORE(true, "g"),
    DIAMOND_ORE(true, "d"),
    REDSTONE_ORE(true, "r"),
    LAPIS_ORE(true, "l");

    private final boolean isOre;

    private final String visual;

    BlockType(boolean isOre, String visual) {
        this.isOre = isOre;
        this.visual = visual;
    }

    public boolean isOre() {
        return isOre;
    }

    public String toString() {
        return visual;
    }
}
