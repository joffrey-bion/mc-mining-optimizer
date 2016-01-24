package org.hildan.minecraft.mining.optimizer.statistics;

import org.hildan.minecraft.mining.optimizer.blocks.Block;
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
            samples[i] = oreGenerator.generate(baseSample, 5);
        }
    }

    public Statistics evaluate(DiggingPattern pattern) {
        Statistics stats = new Statistics(samples.length);
        for (Sample refSample : samples) {
            Sample sample = new Sample(refSample);
            long initialOres = sample.getNumberOfBlocksMatching(Block::isOre);
            stats.totalOres += initialOres;

            pattern.digInto(sample);

            stats.dugBlocks += sample.getNumberOfBlocksMatching(Block::isDug);
            stats.foundOres += initialOres - sample.getNumberOfBlocksMatching(Block::isOre);
        }
        return stats;
    }
}
