package org.hildan.minecraft.mining.optimizer.statistics

import org.hildan.minecraft.mining.optimizer.blocks.Sample
import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern

/**
 * Evaluates digging patterns based on the given reference samples.
 */
class PatternEvaluator(
    private val referenceSamples: List<Sample>
) {
    private val testSamples = referenceSamples.map { Sample(it) }

    private val totalOres = testSamples.map { it.oreBlocksCount }.sum().toLong()

    fun evaluate(pattern: DiggingPattern): EvaluatedPattern {
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
        return EvaluatedPattern(pattern, Statistics(foundOres, dugBlocks, totalOres))
    }
}
