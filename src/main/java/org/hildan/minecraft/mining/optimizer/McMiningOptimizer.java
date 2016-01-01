package org.hildan.minecraft.mining.optimizer;

import org.hildan.minecraft.mining.optimizer.chunks.Chunk;
import org.hildan.minecraft.mining.optimizer.ore.OreGenerator;
import org.hildan.minecraft.mining.optimizer.patterns.BranchingPattern;
import org.hildan.minecraft.mining.optimizer.patterns.DigEverythingPattern;
import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern;
import org.hildan.minecraft.mining.optimizer.patterns.tunnels.TunnelPattern;

public class McMiningOptimizer {

    private static final int ITERATIONS = 20000;

    public static void main(String[] args) {
        Chunk baseChunk = new Chunk(16, 16, 16);
        OreGenerator oreGenerator = new OreGenerator();

        DiggingPattern digEverythingPattern = new DigEverythingPattern();
        System.out.println("DIG EVERYTHING");
        //printPattern(digEverythingPattern);
        printStats(digEverythingPattern, oreGenerator, baseChunk);

        DiggingPattern branchPattern2 = new BranchingPattern(TunnelPattern.STANDARD_SHAFT, TunnelPattern.STANDARD_BRANCH_2SPACED, 11, 2);
        System.out.println("STANDARD BRANCHING - 2 spaced");
        //printPattern(branchPattern2);
        printStats(branchPattern2, oreGenerator, baseChunk);

        DiggingPattern branchPattern3 = new BranchingPattern(TunnelPattern.STANDARD_SHAFT, TunnelPattern.STANDARD_BRANCH_3SPACED, 11, 2);
        System.out.println("STANDARD BRANCHING - 3 spaced");
        //printPattern(branchPattern3);
        printStats(branchPattern3, oreGenerator, baseChunk);
    }

    private static void printPattern(DiggingPattern pattern) {
        System.out.println(pattern.toString());
    }

    private static void printStats(DiggingPattern pattern, OreGenerator oreGenerator, Chunk testChunk) {
        long totalOres = 0;
        long foundOres = 0;
        long dugBlocks = 0;

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < ITERATIONS; i++) {
            Chunk oredChunk = oreGenerator.generate(testChunk, 5);
            long initialOres = oredChunk.getOresCount();
            totalOres += initialOres;

            Chunk dugChunk = pattern.dig(oredChunk);
            dugBlocks += dugChunk.getDugBlocksCount();
            foundOres += initialOres - dugChunk.getOresCount();
        }

        long endTime = System.currentTimeMillis();

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
        System.out.format("Exec. time: %d ms%n", endTime - startTime);
        System.out.println();
    }

}
