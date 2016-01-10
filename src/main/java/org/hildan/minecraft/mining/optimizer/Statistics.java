package org.hildan.minecraft.mining.optimizer;

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

    public static Statistics evaluate(DiggingPattern pattern, OreGenerator oreGenerator, Sample reference,
                                      int nbSamples, boolean debug) {
        Statistics stats = new Statistics(nbSamples);
        for (int i = 0; i < nbSamples; i++) {
            Sample sample = oreGenerator.generate(reference, 5);
            long initialOres = sample.getOresCount();
            stats.totalOres += initialOres;
            if (debug) {
                System.out.println(sample);
            }

            pattern.dig(sample);
            stats.dugBlocks += sample.getDugBlocksCount();
            stats.foundOres += initialOres - sample.getOresCount();
            if (debug) {
                System.out.println(sample);
            }
        }
        return stats;
    }

    public double getEfficiency() {
        return (double) foundOres * 100 / dugBlocks;
    }

    public double getThoroughness() {
        return (double) foundOres * 100 / totalOres;
    }

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
