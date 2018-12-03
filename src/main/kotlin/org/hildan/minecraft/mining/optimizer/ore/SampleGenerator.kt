package org.hildan.minecraft.mining.optimizer.ore

import org.hildan.minecraft.mining.optimizer.blocks.BlockType
import org.hildan.minecraft.mining.optimizer.blocks.Sample
import org.hildan.minecraft.mining.optimizer.geometry.Dimensions

import java.util.EnumMap
import java.util.Random

fun generateSamples(count: Int, dimensions: Dimensions, lowestY: Int, random: Random = Random()): List<Sample> =
    SampleGenerator(dimensions, lowestY, random).generate(count)

/**
 * Generates [Sample]s with ore inside, respecting Minecraft's ore distribution.
 */
private class SampleGenerator(
    private val dimensions: Dimensions,
    lowestY: Int,
    random: Random = Random()
) {
    private val oreInjector: OreInjector = OreInjector(lowestY, random)

    fun generate(count: Int): List<Sample> = List(count) { generate() }

    private fun generate() = Sample(dimensions, BlockType.STONE).also { oreInjector.inject(it) }
}

/**
 * Generates ores into samples, respecting Minecraft's ore distribution.
 */
private class OreInjector(
    private val lowestY: Int,
    private val random: Random = Random()
) {
    /**
     * Replace some stones in the given [sample] by ores. Only stone blocks may be changed.
     */
    fun inject(sample: Sample) {
        for (x in 0 until sample.dimensions.width step CHUNK_WIDTH) {
            for (z in 0 until sample.dimensions.length step CHUNK_LENGTH) {
                genStandardOre(sample, BlockType.COAL_ORE, x, z)
                genStandardOre(sample, BlockType.IRON_ORE, x, z)
                genStandardOre(sample, BlockType.GOLD_ORE, x, z)
                genStandardOre(sample, BlockType.REDSTONE_ORE, x, z)
                genStandardOre(sample, BlockType.DIAMOND_ORE, x, z)
                genLayeredOre(sample, BlockType.LAPIS_ORE, x, z)
            }
        }
    }

    private fun genStandardOre(sample: Sample, type: BlockType, xOffset: Int, zOffset: Int) =
        generateOre(sample, type, xOffset, zOffset) { minY: Int, maxY: Int -> random.nextInt(maxY - minY) + minY }

    private fun genLayeredOre(sample: Sample, type: BlockType, xOffset: Int, zOffset: Int) =
        generateOre(sample, type, xOffset, zOffset) { minY: Int, maxY: Int -> 2 * random.nextInt(maxY) + (minY - maxY) }

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
        repeat (type.veinsCountPerChunk) {
            val x = random.nextInt(CHUNK_WIDTH)
            val y = yGenerator(minY, maxY)
            val z = random.nextInt(CHUNK_LENGTH)

            // coords are generated within a chunk, we check whether it falls in our sample, which is part of a chunk
            // this gives us the same probability as if the sample was actually taken from the Minecraft-generated world
            val xInSample = x + xOffset < sample.dimensions.width + OUTSIDE_MARGIN
            val zInSample = z + zOffset < sample.dimensions.length + OUTSIDE_MARGIN
            val yInSample = lowestY - OUTSIDE_MARGIN <= y && y < lowestY + sample.dimensions.height + OUTSIDE_MARGIN
            if (xInSample && zInSample && yInSample) {
                gen.generateInto(sample, random, x + xOffset, y - lowestY, z + zOffset)
            }
        }
    }

    companion object {
        /** The length of a standard Minecraft chunk. */
        private const val CHUNK_LENGTH = 16

        /** The width of a standard Minecraft chunk. */
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
