package org.hildan.minecraft.mining.optimizer;

import org.hildan.minecraft.mining.optimizer.blocks.Sample;
import org.hildan.minecraft.mining.optimizer.ore.OreGenerator;
import org.hildan.minecraft.mining.optimizer.patterns.BranchingPattern;
import org.hildan.minecraft.mining.optimizer.patterns.DigEverythingPattern;
import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern;
import org.hildan.minecraft.mining.optimizer.patterns.generated.GenerationConstraints;
import org.hildan.minecraft.mining.optimizer.patterns.generated.PatternGenerator;
import org.hildan.minecraft.mining.optimizer.patterns.tunnels.TunnelPattern;
import org.hildan.minecraft.mining.optimizer.statistics.PatternEvaluator;
import org.hildan.minecraft.mining.optimizer.statistics.PatternStore;
import org.hildan.minecraft.mining.optimizer.statistics.Statistics;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

/**
 * Performs statistics on multiple digging patterns to find the most efficient.
 */
public class McMiningOptimizer {

    private static final int NUM_ITERATIONS = 20000;

    private static final int SAMPLE_WIDTH = 8;

    private static final int SAMPLE_HEIGHT = 10;

    private static final int SAMPLE_LENGTH = 8;

    private static final int BRANCH_LENGTH = 11;

    private static final int BRANCH_OFFSET = 2;

    private static final double MARGIN = 0.01d;

    private static final long NANOSECONDS_IN_A_MILLI = 1_000_000L;

    private static final int MAX_ACTIONS = 30;

    private static final int MAX_DUG_BLOCKS = 10;

    public static void main(String... args) {
        Sample reference = new Sample(SAMPLE_WIDTH, SAMPLE_HEIGHT, SAMPLE_LENGTH);

        PatternEvaluator evaluator = new PatternEvaluator(new OreGenerator(), NUM_ITERATIONS, reference);
        PatternStore store = new PatternStore(MARGIN);

        GenerationConstraints constraints = new GenerationConstraints(MAX_ACTIONS, MAX_DUG_BLOCKS);
        Iterable<DiggingPattern> gen = new PatternGenerator(new Sample(reference), constraints);
        for (DiggingPattern pattern : gen) {
            Statistics stats = evaluator.evaluate(pattern);
            if (store.add(pattern, stats)) {
                System.out.println(stats);
            }
        }
    }

    private static void testPatterns(PatternEvaluator evaluator) {
        DiggingPattern digEverythingPattern = new DigEverythingPattern();
        System.out.println("DIG EVERYTHING");
        printStats(digEverythingPattern, evaluator);

        DiggingPattern branchPattern2 =
                new BranchingPattern(TunnelPattern.STANDARD_SHAFT, TunnelPattern.STANDARD_BRANCH_2SPACED, BRANCH_LENGTH,
                        BRANCH_OFFSET);
        System.out.println("STANDARD BRANCHING - 2 spaced");
        printStats(branchPattern2, evaluator);

        DiggingPattern branchPattern3 =
                new BranchingPattern(TunnelPattern.STANDARD_SHAFT, TunnelPattern.STANDARD_BRANCH_3SPACED, BRANCH_LENGTH,
                        BRANCH_OFFSET);
        System.out.println("STANDARD BRANCHING - 3 spaced");
        printStats(branchPattern3, evaluator);

        DiggingPattern branchPatternHighShaft3 =
                new BranchingPattern(TunnelPattern.BIG_SHAFT, TunnelPattern.STANDARD_BRANCH_3SPACED, BRANCH_LENGTH,
                        BRANCH_OFFSET);
        System.out.println("STANDARD BRANCHING (HIGH SHAFT) - 3 spaced");
        printStats(branchPatternHighShaft3, evaluator);
    }

    private static void printStats(DiggingPattern pattern, PatternEvaluator evaluator) {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        long startTime = threadMXBean.getCurrentThreadCpuTime();

        Statistics stats = evaluator.evaluate(pattern);

        long endTime = threadMXBean.getCurrentThreadCpuTime();

        System.out.println(stats);

        long execTimeMillis = (endTime - startTime) / NANOSECONDS_IN_A_MILLI;
        System.out.format("Execution time: %.3fs%n", (double) execTimeMillis / 1000);
        System.out.println();
    }
}
