package org.hildan.minecraft.mining.optimizer;

import org.hildan.minecraft.mining.optimizer.chunks.Sample;
import org.hildan.minecraft.mining.optimizer.ore.OreGenerator;
import org.hildan.minecraft.mining.optimizer.patterns.BranchingPattern;
import org.hildan.minecraft.mining.optimizer.patterns.DigEverythingPattern;
import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern;
import org.hildan.minecraft.mining.optimizer.patterns.tunnels.TunnelPattern;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public class McMiningOptimizer {

    private static final boolean VISUAL_DEBUG = false;

    private static final int ITERATIONS = 10000;

    private static final int SAMPLE_HEIGHT = 16;

    private static final int BRANCH_LENGTH = 11;

    private static final int BRANCH_OFFSET = 2;

    private static final long NANOSECONDS_IN_A_MILLI = 1_000_000L;

    public static void main(String... args) {
        Sample baseSample = new Sample(Sample.CHUNK_WIDTH, SAMPLE_HEIGHT, Sample.CHUNK_LENGTH);
        OreGenerator oreGenerator = new OreGenerator();

        DiggingPattern digEverythingPattern = new DigEverythingPattern();
        System.out.println("DIG EVERYTHING");
        printStats(digEverythingPattern, oreGenerator, baseSample);

        DiggingPattern branchPattern2 =
                new BranchingPattern(TunnelPattern.STANDARD_SHAFT, TunnelPattern.STANDARD_BRANCH_2SPACED, BRANCH_LENGTH,
                        BRANCH_OFFSET);
        System.out.println("STANDARD BRANCHING - 2 spaced");
        printStats(branchPattern2, oreGenerator, baseSample);

        DiggingPattern branchPattern3 =
                new BranchingPattern(TunnelPattern.STANDARD_SHAFT, TunnelPattern.STANDARD_BRANCH_3SPACED, BRANCH_LENGTH,
                        BRANCH_OFFSET);
        System.out.println("STANDARD BRANCHING - 3 spaced");
        printStats(branchPattern3, oreGenerator, baseSample);

        DiggingPattern branchPatternHighShaft3 =
                new BranchingPattern(TunnelPattern.BIG_SHAFT, TunnelPattern.STANDARD_BRANCH_3SPACED, BRANCH_LENGTH,
                        BRANCH_OFFSET);
        System.out.println("STANDARD BRANCHING (HIGH SHAFT) - 3 spaced");
        printStats(branchPatternHighShaft3, oreGenerator, baseSample);
    }

    private static void printStats(DiggingPattern pattern, OreGenerator oreGenerator, Sample testSample) {
        long totalOres = 0;
        long foundOres = 0;
        long dugBlocks = 0;

        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        long startTime = threadMXBean.getCurrentThreadCpuTime();

        for (int i = 0; i < (VISUAL_DEBUG ? 1 : ITERATIONS); i++) {
            Sample sample = oreGenerator.generate(testSample, 5);
            long initialOres = sample.getOresCount();
            totalOres += initialOres;
            if (VISUAL_DEBUG) {
                System.out.println(sample);
            }

            pattern.dig(sample);
            dugBlocks += sample.getDugBlocksCount();
            foundOres += initialOres - sample.getOresCount();
            if (VISUAL_DEBUG) {
                System.out.println(sample);
            }
        }

        long endTime = threadMXBean.getCurrentThreadCpuTime();

        double avgTotalOres = (double) totalOres / ITERATIONS;
        double avgFoundOres = (double) foundOres / ITERATIONS;
        double avgDugBlocks = (double) dugBlocks / ITERATIONS;
        double efficiency = (double) foundOres * 100 / dugBlocks;
        double thoroughness = (double) foundOres * 100 / totalOres;

        System.out.format("            %10s  %12s%n", "Avg/chunk", "Total");
        System.out.format("Total ores: %10.2f  %,12d%n", avgTotalOres, totalOres);
        System.out.format("Found ores: %10.2f  %,12d%n", avgFoundOres, foundOres);
        System.out.format("Dug Blocks: %10.2f  %,12d%n", avgDugBlocks, dugBlocks);
        System.out.println();
        if (dugBlocks == 0) {
            System.out.format("/!\\ This pattern didn't dig anything!%n");
        } else {
            System.out.format("Efficiency:    %6.2f%%%n", efficiency);
            System.out.format("Thoroughness:  %6.2f%%%n", thoroughness);
        }
        System.out.println();
        long execTimeMillis = (endTime - startTime) / NANOSECONDS_IN_A_MILLI;
        System.out.format("Execution time: %.3fs%n", (double) execTimeMillis / 1000);
        System.out.println();
    }
}
