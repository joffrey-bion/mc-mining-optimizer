package org.hildan.minecraft.mining.optimizer;

import org.hildan.minecraft.mining.optimizer.chunks.Sample;
import org.hildan.minecraft.mining.optimizer.ore.OreGenerator;
import org.hildan.minecraft.mining.optimizer.patterns.BranchingPattern;
import org.hildan.minecraft.mining.optimizer.patterns.DigEverythingPattern;
import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern;
import org.hildan.minecraft.mining.optimizer.patterns.tunnels.TunnelPattern;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

/**
 * Performs statistics on multiple digging patterns to find the most efficient.
 */
public class McMiningOptimizer {

    private static final boolean VISUAL_DEBUG = false;

    private static final int NB_ITERATIONS = 10000;

    private static final int SAMPLE_HEIGHT = 10;

    private static final int BRANCH_LENGTH = 11;

    private static final int BRANCH_OFFSET = 2;

    private static final long NANOSECONDS_IN_A_MILLI = 1_000_000L;

    public static void main(String... args) {
        Sample reference = new Sample(Sample.CHUNK_WIDTH, SAMPLE_HEIGHT, Sample.CHUNK_LENGTH);
        OreGenerator oreGenerator = new OreGenerator();

        DiggingPattern digEverythingPattern = new DigEverythingPattern();
        System.out.println("DIG EVERYTHING");
        printStats(digEverythingPattern, oreGenerator, reference);

        DiggingPattern branchPattern2 =
                new BranchingPattern(TunnelPattern.STANDARD_SHAFT, TunnelPattern.STANDARD_BRANCH_2SPACED, BRANCH_LENGTH,
                        BRANCH_OFFSET);
        System.out.println("STANDARD BRANCHING - 2 spaced");
        printStats(branchPattern2, oreGenerator, reference);

        DiggingPattern branchPattern3 =
                new BranchingPattern(TunnelPattern.STANDARD_SHAFT, TunnelPattern.STANDARD_BRANCH_3SPACED, BRANCH_LENGTH,
                        BRANCH_OFFSET);
        System.out.println("STANDARD BRANCHING - 3 spaced");
        printStats(branchPattern3, oreGenerator, reference);

        DiggingPattern branchPatternHighShaft3 =
                new BranchingPattern(TunnelPattern.BIG_SHAFT, TunnelPattern.STANDARD_BRANCH_3SPACED, BRANCH_LENGTH,
                        BRANCH_OFFSET);
        System.out.println("STANDARD BRANCHING (HIGH SHAFT) - 3 spaced");
        printStats(branchPatternHighShaft3, oreGenerator, reference);
    }

    private static void printStats(DiggingPattern pattern, OreGenerator oreGenerator, Sample reference) {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        long startTime = threadMXBean.getCurrentThreadCpuTime();

        final int n = VISUAL_DEBUG ? 1 : NB_ITERATIONS;
        Statistics stats = Statistics.evaluate(pattern, oreGenerator, reference, n, VISUAL_DEBUG);

        long endTime = threadMXBean.getCurrentThreadCpuTime();

        System.out.println(stats);

        long execTimeMillis = (endTime - startTime) / NANOSECONDS_IN_A_MILLI;
        System.out.format("Execution time: %.3fs%n", (double) execTimeMillis / 1000);
        System.out.println();
    }
}
