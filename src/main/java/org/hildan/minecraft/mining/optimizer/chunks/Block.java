package org.hildan.minecraft.mining.optimizer.chunks;

/**
 * Represents the content of a Minecraft block.
 */
public enum Block {
    AIR(false, "."),
    STONE(false, "X"),
    COAL_ORE(true, "c"),
    IRON_ORE(true, "i"),
    GOLD_ORE(true, "g"),
    DIAMOND_ORE(true, "d"),
    REDSTONE_ORE(true, "r"),
    LAPIS_ORE(true, "l");

    private final boolean isOre;

    private final String visual;

    Block(boolean isOre, String visual) {
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
