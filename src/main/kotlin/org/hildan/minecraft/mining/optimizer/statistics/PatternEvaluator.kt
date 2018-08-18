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
    nbEvaluationSamples: Int,
    sampleDimensions: Dimensions,
    sampleYPosition: Int
) {
    private val referenceSamples = generateReferenceSamples(nbEvaluationSamples, sampleDimensions, sampleYPosition)

    private val testSamples = referenceSamples.map { Sample(it) }

    private fun generateReferenceSamples(count: Int, dimensions: Dimensions, lowYPosition: Int): List<Sample> {
        val gen = OreGenerator()
        return List(count) { generateSample(dimensions, lowYPosition, gen) }
    }
    private fun generateSample(dimensions: Dimensions, lowYPosition: Int, oreGenerator: OreGenerator): Sample {
        val sample = Sample(dimensions, BlockType.STONE)
        oreGenerator.generateInto(sample, lowYPosition)
        return sample
    }

    fun evaluate(pattern: DiggingPattern): Statistics {
        val stats = Statistics(referenceSamples.size)
        for (i in testSamples.indices) {
            val testSample = testSamples[i]
            testSample.resetTo(referenceSamples[i])

            val initialOres = testSample.oreBlocksCount.toLong()
            stats.totalOres += initialOres

            pattern.digAndFollowOres(testSample)

            stats.dugBlocks += testSample.dugBlocksCount.toLong()
            stats.foundOres += initialOres - testSample.oreBlocksCount
        }
        return stats
    }
}
