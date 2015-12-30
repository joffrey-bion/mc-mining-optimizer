package org.hildan.minecraft.mining.optimizer;

import org.hildan.minecraft.mining.optimizer.chunks.Chunk;
import org.hildan.minecraft.mining.optimizer.ore.OreGenerator;
import org.hildan.minecraft.mining.optimizer.patterns.DigEverythingPattern;
import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern;

public class McMiningOptimizer {

    private static final int ITERATIONS = 10000;

    public static void main(String[] args) {

        Chunk baseChunk = new Chunk(16, 16, 16);
        OreGenerator oreGenerator = new OreGenerator();
        DiggingPattern pattern = new DigEverythingPattern();

        long totalOres = 0;
        long foundOres = 0;
        long dugBlocks = 0;

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < ITERATIONS; i++) {
            Chunk oredChunk = oreGenerator.generate(baseChunk);
            long initialOres = oredChunk.getOresCount();
            totalOres += initialOres;

            Chunk dugChunk = pattern.dig(oredChunk);
            dugBlocks += dugChunk.getNumberOfDugBlocks();
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
        System.out.format("Efficiency:    %5.2f%%%n", efficiency);
        System.out.format("Thoroughness:  %5.2f%%%n", thoroughness);
        System.out.println();
        System.out.format("Exec. time: %d ms", endTime - startTime);
    }

}
