package org.hildan.minecraft.mining.optimizer.blocks

import org.hildan.minecraft.mining.optimizer.display.AnsiCodes.BLUE
import org.hildan.minecraft.mining.optimizer.display.AnsiCodes.CYAN
import org.hildan.minecraft.mining.optimizer.display.AnsiCodes.RED
import org.hildan.minecraft.mining.optimizer.display.AnsiCodes.YELLOW
import org.hildan.minecraft.mining.optimizer.display.AnsiCodes.color

/**
 * Represents the content of a Minecraft block.
 */
enum class BlockType(
    /**
     * whether this BlockType represents an ore type
     */
    val isOre: Boolean,
    /**
     * the String representation of this BlockType, for printing purposes
     */
    private val visual: String,
    /**
     * the max number of blocks in veins of this type (ore only)
     */
    val maxVeinSize: Int,
    /**
     * the number of generated veins of this type per Minecraft chunk (ore only)
     */
    val veinsCountPerChunk: Int,
    /**
     * the lowest Y where a vein of this type can be generated in a chunk (ore only)
     */
    val minYAvailability: Int,
    /**
     * the highest Y where a vein of this type can be generated in a chunk (ore only)
     */
    val maxYAvailability: Int
) {
    AIR(false, "\u2592", -1, -1, -1, -1),
    STONE(false, "\u2588", -1, -1, -1, -1),
    COAL_ORE(true, "C", 16, 20, 0, 128),
    IRON_ORE(true, "I", 8, 20, 0, 64),
    GOLD_ORE(true, color("G", YELLOW), 8, 2, 0, 32),
    DIAMOND_ORE(true, color("D", CYAN), 7, 1, 0, 16),
    REDSTONE_ORE(true, color("R", RED), 7, 8, 0, 16),
    LAPIS_ORE(true, color("L", BLUE), 6, 1, 16, 16);

    override fun toString(): String {
        return visual
    }
}