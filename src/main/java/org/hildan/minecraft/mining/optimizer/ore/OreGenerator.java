package org.hildan.minecraft.mining.optimizer.ore;

import org.hildan.minecraft.mining.optimizer.chunks.BlockType;
import org.hildan.minecraft.mining.optimizer.chunks.Chunk;

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
     * Generates a copy of the given chunk where some stones are replaced by ores. Only stone blocks may be changed.
     *
     * @param baseChunk
     *         the chunk to start from
     * @param chunkYPosition
     *         the Y index of the lowest layer of the given chunk
     * @return a new chunk based on the given one, containing ores instead of some of the stones.
     */
    public Chunk generate(Chunk baseChunk, int chunkYPosition) {
        this.lowestY = chunkYPosition;

        Chunk chunk = new Chunk(baseChunk);
        this.genStandardOre(chunk, BlockType.COAL_ORE);
        this.genStandardOre(chunk, BlockType.IRON_ORE);
        this.genStandardOre(chunk, BlockType.GOLD_ORE);
        this.genStandardOre(chunk, BlockType.REDSTONE_ORE);
        this.genStandardOre(chunk, BlockType.DIAMOND_ORE);
        this.genLayeredOre(chunk, BlockType.LAPIS_ORE);
        return chunk;
    }

    private void genStandardOre(Chunk chunk, BlockType type) {
        WorldGenMinable gen = oreGenerators.get(type);
        int minY = type.getMinYAvailability();
        int maxY = type.getMaxYAvailability();
        for (int i = 0; i < type.getVeinsCountPerMcChunk(); ++i) {
            int x = random.nextInt(chunk.getWidth());
            int y = random.nextInt(maxY - minY) + minY;
            int z = random.nextInt(chunk.getLength());

            // Y is generated within a true big chunk, we check whether it falls in our chunk, which is a slice of the big chunk
            if (this.lowestY <= y && y < this.lowestY + chunk.getHeight()) {
                gen.generateInto(chunk, random, x, y - this.lowestY, z);
            }
        }
    }

    private void genLayeredOre(Chunk chunk, BlockType type) {
        WorldGenMinable gen = oreGenerators.get(type);
        int minY = type.getMinYAvailability();
        int maxY = type.getMaxYAvailability();
        for (int i = 0; i < type.getVeinsCountPerMcChunk(); ++i) {
            int x = random.nextInt(chunk.getWidth());
            int y = 2 * random.nextInt(maxY) + (minY - maxY);
            int z = random.nextInt(chunk.getLength());

            if (this.lowestY <= y && y < this.lowestY + chunk.getHeight()) {
                gen.generateInto(chunk, random, x, y - this.lowestY, z);
            }
        }
    }
}
