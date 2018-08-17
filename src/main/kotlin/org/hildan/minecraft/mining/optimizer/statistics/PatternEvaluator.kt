package org.hildan.minecraft.mining.optimizer.statistics

import org.hildan.minecraft.mining.optimizer.blocks.BlockType
import org.hildan.minecraft.mining.optimizer.blocks.Sample
import org.hildan.minecraft.mining.optimizer.geometry.Dimensions
import org.hildan.minecraft.mining.optimizer.ore.OreGenerator
import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern

/**
 * Creates a pool of Samples to evaluate digging patterns.
 */
class PatternEvaluator(
    oreGenerator: OreGenerator,
    nbEvaluationSamples: Int,
    sampleDimensions: Dimensions,
    sampleYPosition: Int
) {
    private val samples = List(nbEvaluationSamples) { generateSample(sampleDimensions, oreGenerator, sampleYPosition) }

    private fun generateSample(
        sampleDimensions: Dimensions,
        oreGenerator: OreGenerator,
        yPosition: Int
    ): Sample {
        val sample = Sample(sampleDimensions, BlockType.STONE)
        oreGenerator.generateInto(sample, yPosition)
        return sample
    }

    fun evaluate(pattern: DiggingPattern): Statistics {
        val stats = Statistics(samples.size)
        for (refSample in samples) {
            val sample = Sample(refSample)
            val initialOres = sample.oreBlocksCount.toLong()
            stats.totalOres += initialOres

            pattern.digInto(sample)

            stats.dugBlocks += sample.dugBlocksCount.toLong()
            stats.foundOres += initialOres - sample.oreBlocksCount
        }
        return stats
    }
}
