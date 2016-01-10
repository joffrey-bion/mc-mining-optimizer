package org.hildan.minecraft.mining.optimizer.ore;

import org.hildan.minecraft.mining.optimizer.chunks.BlockType;
import org.hildan.minecraft.mining.optimizer.chunks.Sample;

import java.util.EnumMap;
import java.util.Random;

public class OreGenerator {

    private final Random random = new Random();

    private int lowestY;

    private static final EnumMap<BlockType, WorldGenMinable> oreGenerators = new EnumMap<>(BlockType.class);

    static {
        for (BlockType type : BlockType.values()) {
            if (type.isOre()) {
                oreGenerators.put(type, new WorldGenMinable(type));
            }
        }
    }

    /**
     * Generates a copy of the given sample where some stones are replaced by ores. Only stone blocks may be changed.
     *
     * @param baseSample
     *         the sample to start from
     * @param sampleYPosition
     *         the Y index of the lowest layer of the given sample within the chunk
     * @return a new sample based on the given one, containing ores instead of some of the stones.
     */
    public Sample generate(Sample baseSample, int sampleYPosition) {
        this.lowestY = sampleYPosition;

        Sample sample = new Sample(baseSample);
        genStandardOre(sample, BlockType.COAL_ORE);
        genStandardOre(sample, BlockType.IRON_ORE);
        genStandardOre(sample, BlockType.GOLD_ORE);
        genStandardOre(sample, BlockType.REDSTONE_ORE);
        genStandardOre(sample, BlockType.DIAMOND_ORE);
        genLayeredOre(sample, BlockType.LAPIS_ORE);
        return sample;
    }

    private void genStandardOre(Sample sample, BlockType type) {
        WorldGenMinable gen = oreGenerators.get(type);
        int minY = type.getMinYAvailability();
        int maxY = type.getMaxYAvailability();
        for (int i = 0; i < type.getVeinsCountPerChunk(); ++i) {
            int x = random.nextInt(sample.getWidth());
            int y = random.nextInt(maxY - minY) + minY;
            int z = random.nextInt(sample.getLength());

            // Y is generated within a chunk, we check whether it falls in our sample, which is a slice of the chunk
            // this gives us the same probability as if the sample was actually taken from the Minecraft-generated world
            if (lowestY <= y && y < lowestY + sample.getHeight()) {
                gen.generateInto(sample, random, x, y - lowestY, z);
            }
        }
    }

    private void genLayeredOre(Sample sample, BlockType type) {
        WorldGenMinable gen = oreGenerators.get(type);
        int minY = type.getMinYAvailability();
        int maxY = type.getMaxYAvailability();
        for (int i = 0; i < type.getVeinsCountPerChunk(); ++i) {
            int x = random.nextInt(sample.getWidth());
            int y = 2 * random.nextInt(maxY) + (minY - maxY);
            int z = random.nextInt(sample.getLength());

            if (lowestY <= y && y < lowestY + sample.getHeight()) {
                gen.generateInto(sample, random, x, y - lowestY, z);
            }
        }
    }
}
