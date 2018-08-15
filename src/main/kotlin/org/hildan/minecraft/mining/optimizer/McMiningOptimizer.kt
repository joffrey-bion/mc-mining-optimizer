package org.hildan.minecraft.mining.optimizer

import org.hildan.minecraft.mining.optimizer.blocks.Sample
import org.hildan.minecraft.mining.optimizer.ore.OreGenerator
import org.hildan.minecraft.mining.optimizer.patterns.DigEverythingPattern
import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern
import org.hildan.minecraft.mining.optimizer.patterns.generated.GenerationConstraints
import org.hildan.minecraft.mining.optimizer.patterns.generated.PatternGenerator
import org.hildan.minecraft.mining.optimizer.patterns.tunnels.BranchingPattern
import org.hildan.minecraft.mining.optimizer.patterns.tunnels.TunnelPattern.Companion.BIG_SHAFT
import org.hildan.minecraft.mining.optimizer.patterns.tunnels.TunnelPattern.Companion.STANDARD_BRANCH_2SPACED
import org.hildan.minecraft.mining.optimizer.patterns.tunnels.TunnelPattern.Companion.STANDARD_BRANCH_3SPACED
import org.hildan.minecraft.mining.optimizer.patterns.tunnels.TunnelPattern.Companion.STANDARD_SHAFT
import org.hildan.minecraft.mining.optimizer.statistics.PatternEvaluator
import org.hildan.minecraft.mining.optimizer.statistics.PatternStore
import java.lang.management.ManagementFactory

private const val NUM_ITERATIONS = 20000

private const val SAMPLE_WIDTH = 16

private const val SAMPLE_HEIGHT = 5

private const val SAMPLE_LENGTH = 16

private const val BRANCH_LENGTH = 11

private const val BRANCH_OFFSET = 2

private const val MARGIN = 0.001

private const val NANOSECONDS_IN_A_MILLI = 1_000_000L

private const val MAX_ACTIONS = 30

private const val MAX_DUG_BLOCKS = 20

fun main(vararg args: String) {
    val reference = Sample(SAMPLE_WIDTH, SAMPLE_HEIGHT, SAMPLE_LENGTH)

    System.out.printf("Initializing evaluator for %d iterations...%n", NUM_ITERATIONS)
    val evaluator = PatternEvaluator(OreGenerator(), NUM_ITERATIONS, reference)

    System.out.printf("Initializing store with margin of %f%n", MARGIN)
    val store = PatternStore(MARGIN)

    System.out.printf("Initializing constraints: %d actions max, %d dug blocks max%n", MAX_ACTIONS, MAX_DUG_BLOCKS)
    val constraints = GenerationConstraints(MAX_ACTIONS, MAX_DUG_BLOCKS)

    System.out.println("Initializing pattern generator...")
    val gen = PatternGenerator(Sample(reference), constraints)

    System.out.println("Start!")
    for (pattern in gen) {
        val stats = evaluator.evaluate(pattern)
        if (store.add(pattern, stats)) {
            System.out.println(store)
        }
    }

    System.out.printf("Finished!%n%n")
    for (pattern in store) {
        System.out.println(pattern.pattern)
        System.out.println(pattern.statistics.toFullString())
    }
}

private fun testPatterns(evaluator: PatternEvaluator) {
    val digEverythingPattern = DigEverythingPattern()
    System.out.println("DIG EVERYTHING")
    printStats(digEverythingPattern, evaluator)

    val branchPattern2 = BranchingPattern(STANDARD_SHAFT, STANDARD_BRANCH_2SPACED, BRANCH_LENGTH, BRANCH_OFFSET)
    System.out.println("STANDARD BRANCHING - 2 spaced")
    printStats(branchPattern2, evaluator)

    val branchPattern3 = BranchingPattern(STANDARD_SHAFT, STANDARD_BRANCH_3SPACED, BRANCH_LENGTH, BRANCH_OFFSET)
    System.out.println("STANDARD BRANCHING - 3 spaced")
    printStats(branchPattern3, evaluator)

    val branchPatternHighShaft3 = BranchingPattern(BIG_SHAFT, STANDARD_BRANCH_3SPACED, BRANCH_LENGTH, BRANCH_OFFSET)
    System.out.println("STANDARD BRANCHING (HIGH SHAFT) - 3 spaced")
    printStats(branchPatternHighShaft3, evaluator)
}

private fun printStats(pattern: DiggingPattern, evaluator: PatternEvaluator) {
    val threadMXBean = ManagementFactory.getThreadMXBean()
    val startTime = threadMXBean.currentThreadCpuTime

    val stats = evaluator.evaluate(pattern)

    val endTime = threadMXBean.currentThreadCpuTime

    System.out.println(stats)

    val execTimeMillis = (endTime - startTime) / NANOSECONDS_IN_A_MILLI
    System.out.format("Execution time: %.3fs%n", execTimeMillis.toDouble() / 1000)
    System.out.println()
}
