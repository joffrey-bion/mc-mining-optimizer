package org.hildan.minecraft.mining.optimizer.statistics;

import java.util.Locale;

/**
 * Represents statistics about a digging pattern.
 */
public class Statistics {

    private final int nbSamples;

    long totalOres = 0;

    long foundOres = 0;

    long dugBlocks = 0;

    Statistics(int nbSamples) {
        this.nbSamples = nbSamples;
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
        return String.format(Locale.US, "e=%.2f%% t=%.2f%%", getEfficiency(), getThoroughness());
    }

    public String toFullString() {
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
