package org.hildan.minecraft.mining.optimizer.statistics

import org.hildan.minecraft.mining.optimizer.blocks.Sample
import org.hildan.minecraft.mining.optimizer.ore.OreGenerator
import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern

/**
 * Creates a pool of Samples to evaluate digging patterns.
 */
class PatternEvaluator(oreGenerator: OreGenerator, nbSamples: Int, baseSample: Sample) {

    private val samples: List<Sample>

    init {
        samples = List(nbSamples) {
            print("\rGenerating sample ${it + 1}/$nbSamples...")
            generateSample(baseSample, oreGenerator)
        }
        println("\rAll $nbSamples samples generated")
    }

    private fun generateSample(baseSample: Sample, oreGenerator: OreGenerator): Sample {
        val sample = Sample(baseSample)
        oreGenerator.generateInto(sample, 5)
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
