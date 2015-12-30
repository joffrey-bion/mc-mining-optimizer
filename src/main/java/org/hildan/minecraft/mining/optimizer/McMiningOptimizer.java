package org.hildan.minecraft.mining.optimizer;

import org.hildan.minecraft.mining.optimizer.chunks.Chunk;
import org.hildan.minecraft.mining.optimizer.chunks.DugChunk;
import org.hildan.minecraft.mining.optimizer.chunks.OredChunk;
import org.hildan.minecraft.mining.optimizer.patterns.DigEverythingPattern;
import org.hildan.minecraft.mining.optimizer.patterns.Pattern;

public class McMiningOptimizer {

    private static final int ITERATIONS = 500;

    public static void main(String[] args) {

        Chunk baseChunk = new Chunk(16, 16, 16);
        OreGenerator oreGenerator = new OreGenerator();
        Pattern pattern = new DigEverythingPattern();

        long totalOres = 0;
        long foundOres = 0;
        long dugBlocks = 0;
        for (int i = 0; i < ITERATIONS; i++) {
            OredChunk oredChunk = oreGenerator.generate(baseChunk);
            long initialOres = oredChunk.getNumberOfOres();
            totalOres += initialOres;

            DugChunk dugChunk = pattern.dig(oredChunk);
            dugBlocks += dugChunk.getNumberOfDugBlocks();
            foundOres += initialOres - dugChunk.getNumberOfOres();
        }
        double avgTotalOres = (double) totalOres / ITERATIONS;
        double avgFoundOres = (double) foundOres / ITERATIONS;
        double avgDugBlocks = (double) dugBlocks / ITERATIONS;
        double efficiency = (double) foundOres * 100 / dugBlocks;
        double thoroughness = (double) foundOres * 100 / totalOres;
        System.out.format("            %9s  %9s%n", "Avg/chunk", "Total");
        System.out.format("Total ores: %2.6f  %9d%n", avgTotalOres, totalOres);
        System.out.format("Found ores: %2.6f  %9d%n", avgFoundOres, foundOres);
        System.out.format("Dug Blocks: %2.6f  %9d%n%n", avgDugBlocks, dugBlocks);
        System.out.format("Efficiency:   %3.2f%%%n", efficiency);
        System.out.format("Thoroughness: %3.2f%%%n", thoroughness);
    }

}
