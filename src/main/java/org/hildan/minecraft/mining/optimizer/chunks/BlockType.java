package org.hildan.minecraft.mining.optimizer.chunks;

/**
 * Represents the content of a Minecraft block.
 */
public enum BlockType {

    AIR(false, "\u2592"),
    STONE(false, "\u2588"),
    COAL_ORE(true, "C"),
    IRON_ORE(true, "I"),
    GOLD_ORE(true, "G"),
    DIAMOND_ORE(true, "D"),
    REDSTONE_ORE(true, "R"),
    LAPIS_ORE(true, "L");

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
