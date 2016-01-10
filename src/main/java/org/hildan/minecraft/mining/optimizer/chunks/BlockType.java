package org.hildan.minecraft.mining.optimizer.chunks;

import static org.hildan.minecraft.mining.optimizer.display.AnsiCodes.*;

/**
 * Represents the content of a Minecraft block.
 */
public enum BlockType {

    AIR(false, "\u2592", -1, -1, -1, -1),
    STONE(false, "\u2588", -1, -1, -1, -1),
    COAL_ORE(true, "C", 16, 20, 0, 128),
    IRON_ORE(true, "I", 8, 20, 0, 64),
    GOLD_ORE(true, color("G", YELLOW), 8, 2, 0, 32),
    DIAMOND_ORE(true, color("D", CYAN), 7, 1, 0, 16),
    REDSTONE_ORE(true, color("R", RED), 7, 8, 0, 16),
    LAPIS_ORE(true, color("L", BLUE), 6, 1, 16, 16);

    private final boolean isOre;

    private final String visual;

    private final int maxVeinSize;

    private final int veinsCountPerChunk;

    private final int minYAvailability;

    private final int maxYAvailability;

    /**
     * Creates a new BLockType.
     *
     * @param isOre
     *         whether this BlockType represents an ore type
     * @param visual
     *         the String representation of this BlockType, for printing purposes
     * @param maxVeinSize
     *         the max number of blocks in vein of this type (ore only)
     * @param veinsCountPerChunk
     *         the number of generated veins of this type per Minecraft chunk (ore only)
     * @param minY
     *         the lowest Y where a vein of this type can be generated in a chunk (ore only)
     * @param maxY
     *         the highest Y where a vein of this type can be generated in a chunk (ore only)
     */
    BlockType(boolean isOre, String visual, int maxVeinSize, int veinsCountPerChunk, int minY, int maxY) {
        this.isOre = isOre;
        this.visual = visual;
        this.maxVeinSize = maxVeinSize;
        this.veinsCountPerChunk = veinsCountPerChunk;
        this.minYAvailability = minY;
        this.maxYAvailability = maxY;
    }

    /**
     * @return true if this type represents an ore type.
     */
    public boolean isOre() {
        return isOre;
    }

    /**
     * @return the max number of blocks in vein of this type (ore only)
     */
    public int getMaxVeinSize() {
        return maxVeinSize;
    }

    /**
     * @return the number of generated veins of this type per Minecraft chunk (ore only)
     */
    public int getVeinsCountPerChunk() {
        return veinsCountPerChunk;
    }

    /**
     * @return the lowest Y where a vein of this type can be generated in a chunk (ore only)
     */
    public int getMinYAvailability() {
        return minYAvailability;
    }

    /**
     * @return the highest Y where a vein of this type can be generated in a chunk (ore only)
     */
    public int getMaxYAvailability() {
        return maxYAvailability;
    }

    @Override
    public String toString() {
        return visual;
    }
}
