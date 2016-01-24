package org.hildan.minecraft.mining.optimizer.statistics;

import org.hildan.minecraft.mining.optimizer.chunks.Block;
import org.hildan.minecraft.mining.optimizer.chunks.Sample;
import org.hildan.minecraft.mining.optimizer.ore.OreGenerator;
import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern;

/**
 * Represents statistics about a digging pattern.
 */
public class Statistics {

    private final int nbSamples;

    private long totalOres = 0;

    private long foundOres = 0;

    private long dugBlocks = 0;

    private Statistics(int nbSamples) {
        this.nbSamples = nbSamples;
    }

    public static Statistics evaluate(DiggingPattern pattern, OreGenerator oreGenerator, Sample base, int nbSamples) {
        Statistics stats = new Statistics(nbSamples);
        for (int i = 0; i < nbSamples; i++) {
            Sample sample = oreGenerator.generate(base, 5);
            long initialOres = sample.getNumberOfBlocksMatching(Block::isOre);
            stats.totalOres += initialOres;

            pattern.digInto(sample);
            stats.dugBlocks += sample.getNumberOfBlocksMatching(Block::isDug);
            stats.foundOres += initialOres - sample.getNumberOfBlocksMatching(Block::isOre);
        }
        return stats;
    }

    private double getEfficiency() {
        return dugBlocks == 0 ? 100 : (double) foundOres * 100 / dugBlocks;
    }

    private double getThoroughness() {
        return totalOres == 0 ? 100 : (double) foundOres * 100 / totalOres;
    }

    boolean isBetterThan(Statistics stats, double margin) {
        double eff = getEfficiency();
        double tho = getThoroughness();
        double effOther = stats.getEfficiency();
        double thoOther = stats.getThoroughness();

        return eff > effOther + margin && tho > thoOther + margin;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        double avgTotalOres = (double) totalOres / nbSamples;
        double avgFoundOres = (double) foundOres / nbSamples;
        double avgDugBlocks = (double) dugBlocks / nbSamples;

        sb.append(String.format("            %10s  %12s%n", "Avg/sample", "Total"));
        sb.append(String.format("Total ores: %10.2f  %,12d%n", avgTotalOres, totalOres));
        sb.append(String.format("Found ores: %10.2f  %,12d%n", avgFoundOres, foundOres));
        sb.append(String.format("Dug Blocks: %10.2f  %,12d%n", avgDugBlocks, dugBlocks));
        sb.append(String.format("%n"));
        if (dugBlocks == 0) {
            sb.append(String.format("/!\\ The pattern didn't dig anything!%n"));
        } else {
            sb.append(String.format("Efficiency:    %6.2f%%%n", getEfficiency()));
            sb.append(String.format("Thoroughness:  %6.2f%%%n", getThoroughness()));
        }
        return sb.toString();
    }
}
