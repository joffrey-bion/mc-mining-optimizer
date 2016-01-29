package org.hildan.minecraft.mining.optimizer.statistics;

import org.hildan.minecraft.mining.optimizer.blocks.Sample;
import org.hildan.minecraft.mining.optimizer.ore.OreGenerator;
import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern;

/**
 * Creates a pool of Samples to evaluate digging patterns.
 */
public class PatternEvaluator {

    private final Sample[] samples;

    public PatternEvaluator(OreGenerator oreGenerator, int nbSamples, Sample baseSample) {
        samples = new Sample[nbSamples];
        for (int i = 0; i < nbSamples; i++) {
            //noinspection HardcodedLineSeparator
            System.out.printf("\r%s generated samples", i);
            samples[i] = new Sample(baseSample);
            oreGenerator.generateInto(samples[i], 5);
        }
        System.out.println();
    }

    public Statistics evaluate(DiggingPattern pattern) {
        Statistics stats = new Statistics(samples.length);
        for (Sample refSample : samples) {
            Sample sample = new Sample(refSample);
            long initialOres = sample.getOreBlocksCount();
            stats.totalOres += initialOres;

            pattern.digInto(sample);

            stats.dugBlocks += sample.getDugBlocksCount();
            stats.foundOres += initialOres - sample.getOreBlocksCount();
        }
        return stats;
    }
}
