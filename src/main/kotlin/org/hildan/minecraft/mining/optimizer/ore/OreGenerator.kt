package org.hildan.minecraft.mining.optimizer.ore

import org.hildan.minecraft.mining.optimizer.blocks.BlockType
import org.hildan.minecraft.mining.optimizer.blocks.Sample

import java.util.EnumMap
import java.util.Random

class OreGenerator {

    private val random = Random()

    private var lowestY: Int = 0

    /**
     * Replace some stones in the given sample by ores. Only stone blocks may be changed.
     *
     * @param sample the sample to put ore into
     * @param yPosition the Y index of the lowest layer of the given sample within the chunk
     */
    fun generateInto(sample: Sample, yPosition: Int) {
        this.lowestY = yPosition

        for (x in 0 until sample.width step CHUNK_WIDTH) {
            for (z in 0 until sample.length step CHUNK_LENGTH) {
                genStandardOre(sample, BlockType.COAL_ORE, x, z)
                genStandardOre(sample, BlockType.IRON_ORE, x, z)
                genStandardOre(sample, BlockType.GOLD_ORE, x, z)
                genStandardOre(sample, BlockType.REDSTONE_ORE, x, z)
                genStandardOre(sample, BlockType.DIAMOND_ORE, x, z)
                genLayeredOre(sample, BlockType.LAPIS_ORE, x, z)
            }
        }
    }

    private fun genStandardOre(sample: Sample, type: BlockType, xOffset: Int, zOffset: Int) {
        val yGenerator = { minY: Int, maxY: Int -> random.nextInt(maxY - minY) + minY }
        generateOre(sample, type, xOffset, zOffset, yGenerator)
    }

    private fun genLayeredOre(sample: Sample, type: BlockType, xOffset: Int, zOffset: Int) {
        val yGenerator = { minY: Int, maxY: Int -> 2 * random.nextInt(maxY) + (minY - maxY) }
        generateOre(sample, type, xOffset, zOffset, yGenerator)
    }

    private fun generateOre(
        sample: Sample,
        type: BlockType,
        xOffset: Int,
        zOffset: Int,
        yGenerator: (minY: Int, maxY: Int) -> Int
    ) {
        val gen = oreGenerators[type]!!
        val minY = type.minYAvailability
        val maxY = type.maxYAvailability
        for (i in 0 until type.veinsCountPerChunk) {
            val x = random.nextInt(CHUNK_WIDTH)
            val y = yGenerator(minY, maxY)
            val z = random.nextInt(CHUNK_LENGTH)

            // coords are generated within a chunk, we check whether it falls in our sample, which is part of a chunk
            // this gives us the same probability as if the sample was actually taken from the Minecraft-generated world
            if (x + xOffset < sample.width + OUTSIDE_MARGIN && z + zOffset < sample.length + OUTSIDE_MARGIN) {
                if (lowestY - OUTSIDE_MARGIN <= y && y < lowestY + sample.height + OUTSIDE_MARGIN) {
                    gen.generateInto(sample, random, x + xOffset, y - lowestY, z + zOffset)
                }
            }
        }
    }

    companion object {

        /**
         * The length of a standard Minecraft chunk.
         */
        private const val CHUNK_LENGTH = 16

        /**
         * The width of a standard Minecraft chunk.
         */
        private const val CHUNK_WIDTH = 16

        /**
         * The allowed margin to generate ore outside the sample. The generation might create ores inside the sample even if
         * the center is outside.
         */
        private const val OUTSIDE_MARGIN = 5

        private val oreGenerators = EnumMap<BlockType, WorldGenMinable>(BlockType::class.java)

        init {
            for (type in BlockType.values()) {
                if (type.isOre) {
                    oreGenerators[type] = WorldGenMinable(type)
                }
            }
        }
    }
}
