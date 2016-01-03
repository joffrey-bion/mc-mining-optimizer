package org.hildan.minecraft.mining.optimizer.chunks;

/**
 * Represents the content of a Minecraft block.
 */
public enum BlockType {

    AIR(false, "\u2592", -1, -1, -1, -1),
    STONE(false, "\u2588", -1, -1, -1, -1),
    COAL_ORE(true, "C", 16, 20, 0, 128),
    IRON_ORE(true, "I", 8, 20, 0, 64),
    GOLD_ORE(true, "G", 8, 2, 0, 32),
    DIAMOND_ORE(true, "D", 7, 1, 0, 16),
    REDSTONE_ORE(true, "R", 7, 8, 0, 16),
    LAPIS_ORE(true, "L", 6, 1, 16, 16);

    private final boolean isOre;

    private final String visual;

    private final int maxVeinSize;

    private final int veinsCountPerMcChunk;

    private final int minYAvailability;

    private final int maxYAvailability;

    BlockType(boolean isOre, String visual, int maxVeinSize, int veinsCountPerMcChunk, int minY, int maxY) {
        this.isOre = isOre;
        this.visual = visual;
        this.maxVeinSize = maxVeinSize;
        this.veinsCountPerMcChunk = veinsCountPerMcChunk;
        this.minYAvailability = minY;
        this.maxYAvailability = maxY;
    }

    public boolean isOre() {
        return isOre;
    }

    public int getMaxVeinSize() {
        return maxVeinSize;
    }

    public int getVeinsCountPerMcChunk() {
        return veinsCountPerMcChunk;
    }

    public int getMinYAvailability() {
        return minYAvailability;
    }

    public int getMaxYAvailability() {
        return maxYAvailability;
    }

    public String toString() {
        return visual;
    }
}
