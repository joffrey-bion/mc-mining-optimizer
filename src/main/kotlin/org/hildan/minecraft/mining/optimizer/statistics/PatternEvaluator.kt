package org.hildan.minecraft.mining.optimizer.statistics

import org.hildan.minecraft.mining.optimizer.blocks.Sample
import org.hildan.minecraft.mining.optimizer.geometry.Dimensions
import org.hildan.minecraft.mining.optimizer.ore.SampleGenerator
import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern

/**
 * Evaluates digging patterns based on a number of randomly generated samples.
 */
class PatternEvaluator(
    nbEvaluationSamples: Int,
    sampleDimensions: Dimensions,
    sampleLowestY: Int
) {
    private val referenceSamples = SampleGenerator(sampleDimensions, sampleLowestY).generate(nbEvaluationSamples)

    private val testSamples = referenceSamples.map { Sample(it) }

    private val totalOres = testSamples.map { it.oreBlocksCount }.sum().toLong()

    fun evaluate(pattern: DiggingPattern): Statistics {
        var dugBlocks = 0L
        var foundOres = 0L
        for (i in testSamples.indices) {
            val testSample = testSamples[i]
            testSample.resetTo(referenceSamples[i])

            val initialOres = testSample.oreBlocksCount.toLong()

            pattern.digAndFollowOres(testSample)

            dugBlocks += testSample.dugBlocksCount.toLong()
            foundOres += initialOres - testSample.oreBlocksCount
        }
        return Statistics(foundOres, dugBlocks, totalOres)
    }
}
