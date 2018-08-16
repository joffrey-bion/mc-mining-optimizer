package org.hildan.minecraft.mining.optimizer.statistics

import org.hildan.minecraft.mining.optimizer.blocks.Sample
import org.hildan.minecraft.mining.optimizer.ore.OreGenerator
import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern

/**
 * Creates a pool of Samples to evaluate digging patterns.
 */
class PatternEvaluator(
    oreGenerator: OreGenerator,
    nbSamples: Int,
    baseSample: Sample,
    sampleYPosition: Int
) {
    private val samples = List(nbSamples) { generateSample(baseSample, oreGenerator, sampleYPosition) }

    private fun generateSample(
        baseSample: Sample,
        oreGenerator: OreGenerator,
        yPosition: Int
    ): Sample {
        val sample = Sample(baseSample)
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
