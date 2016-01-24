package org.hildan.minecraft.mining.optimizer.ore;

import org.hildan.minecraft.mining.optimizer.blocks.BlockType;
import org.hildan.minecraft.mining.optimizer.blocks.Sample;

import java.util.EnumMap;
import java.util.Random;

public class OreGenerator {

    /**
     * The length of a standard Minecraft chunk.
     */
    private static final int CHUNK_LENGTH = 16;

    /**
     * The width of a standard Minecraft chunk.
     */
    private static final int CHUNK_WIDTH = 16;

    /**
     * The allowed margin to generate ore outside the sample. The generation might create ores inside the sample even if
     * the center is outside.
     */
    private static final int OUTSIDE_MARGIN = 5;

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
        for (int x = 0; x < sample.getWidth(); x += CHUNK_WIDTH) {
            for (int z = 0; z < sample.getLength(); z += CHUNK_LENGTH) {
                genStandardOre(sample, BlockType.COAL_ORE, x, z);
                genStandardOre(sample, BlockType.IRON_ORE, x, z);
                genStandardOre(sample, BlockType.GOLD_ORE, x, z);
                genStandardOre(sample, BlockType.REDSTONE_ORE, x, z);
                genStandardOre(sample, BlockType.DIAMOND_ORE, x, z);
                genLayeredOre(sample, BlockType.LAPIS_ORE, x, z);
            }
        }
        return sample;
    }

    private void genStandardOre(Sample sample, BlockType type, int xOffset, int zOffset) {
        WorldGenMinable gen = oreGenerators.get(type);
        int minY = type.getMinYAvailability();
        int maxY = type.getMaxYAvailability();
        for (int i = 0; i < type.getVeinsCountPerChunk(); ++i) {
            int x = random.nextInt(CHUNK_WIDTH);
            int y = random.nextInt(maxY - minY) + minY;
            int z = random.nextInt(CHUNK_LENGTH);

            // coords are generated within a chunk, we check whether it falls in our sample, which is part of a chunk
            // this gives us the same probability as if the sample was actually taken from the Minecraft-generated world
            if (x + xOffset < sample.getWidth() + OUTSIDE_MARGIN && z + zOffset < sample.getLength() + OUTSIDE_MARGIN) {
                if (lowestY - OUTSIDE_MARGIN <= y && y < lowestY + sample.getHeight() + OUTSIDE_MARGIN) {
                    gen.generateInto(sample, random, x + xOffset, y - lowestY, z + zOffset);
                }
            }
        }
    }

    private void genLayeredOre(Sample sample, BlockType type, int xOffset, int zOffset) {
        WorldGenMinable gen = oreGenerators.get(type);
        int minY = type.getMinYAvailability();
        int maxY = type.getMaxYAvailability();
        for (int i = 0; i < type.getVeinsCountPerChunk(); ++i) {
            int x = random.nextInt(CHUNK_WIDTH);
            int y = 2 * random.nextInt(maxY) + (minY - maxY);
            int z = random.nextInt(CHUNK_LENGTH);

            // coords are generated within a chunk, we check whether it falls in our sample, which is part of a chunk
            // this gives us the same probability as if the sample was actually taken from the Minecraft-generated world
            if (x + xOffset < sample.getWidth() + OUTSIDE_MARGIN && z + zOffset < sample.getLength() + OUTSIDE_MARGIN) {
                if (lowestY - OUTSIDE_MARGIN <= y && y < lowestY + sample.getHeight() + OUTSIDE_MARGIN) {
                    gen.generateInto(sample, random, x + xOffset, y - lowestY, z + zOffset);
                }
            }
        }
    }
}
