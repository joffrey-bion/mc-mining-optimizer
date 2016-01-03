package org.hildan.minecraft.mining.optimizer.ore;

import org.hildan.minecraft.mining.optimizer.chunks.BlockType;
import org.hildan.minecraft.mining.optimizer.chunks.Chunk;

import java.util.Random;

public class OreGenerator {

    private Random random = new Random();

    private int lowestY;

    private WorldGenMinable coalGen = new WorldGenMinable(BlockType.COAL_ORE, 16);

    private WorldGenMinable ironGen = new WorldGenMinable(BlockType.IRON_ORE, 8);

    private WorldGenMinable goldGen = new WorldGenMinable(BlockType.GOLD_ORE, 8);

    private WorldGenMinable redstoneGen = new WorldGenMinable(BlockType.REDSTONE_ORE, 7);

    private WorldGenMinable diamondGen = new WorldGenMinable(BlockType.DIAMOND_ORE, 7);

    private WorldGenMinable lapisGen = new WorldGenMinable(BlockType.LAPIS_ORE, 6);

    /**
     * Generates a copy of the given chunk where some stones are replaced by ores. Only stone blocks may be changed.
     *
     * @param baseChunk
     *     the chunk to start from
     * @param chunkYPosition
     *     the Y index of the lowest layer of the given chunk
     *
     * @return a new chunk based on the given one, containing ores instead of some of the stones.
     */
    public Chunk generate(Chunk baseChunk, int chunkYPosition) {
        this.lowestY = chunkYPosition;

        Chunk oredChunk = new Chunk(baseChunk);
        this.genStandardOre(oredChunk, 20, this.coalGen, 0, 128);
        this.genStandardOre(oredChunk, 20, this.ironGen, 0, 64);
        this.genStandardOre(oredChunk, 2, this.goldGen, 0, 32);
        this.genStandardOre(oredChunk, 8, this.redstoneGen, 0, 16);
        this.genStandardOre(oredChunk, 1, this.diamondGen, 0, 16);
        this.genSingleLayerOre(oredChunk, 1, this.lapisGen, 16, 16);
        return oredChunk;
    }

    protected void genStandardOre(Chunk oredChunk, int nVeinsPerBigChunk, WorldGenMinable generator, int minY, int maxY) {
        for (int i = 0; i < nVeinsPerBigChunk; ++i) {
            int x = random.nextInt(oredChunk.getWidth());
            int y = random.nextInt(maxY - minY) + minY;
            int z = random.nextInt(oredChunk.getLength());

            // Y is generated within a true big chunk, we check whether it falls in our chunk, which is a slice of the big chunk
            if (this.lowestY <= y && y < this.lowestY + oredChunk.getHeight()) {
                generator.generateInto(oredChunk, random, x, y - this.lowestY, z);
            }
        }
    }

    protected void genSingleLayerOre(Chunk oredChunk, int nVeinsPerBigChunk, WorldGenMinable generator, int minY, int maxY) {
        for (int i = 0; i < nVeinsPerBigChunk; ++i) {
            int x = random.nextInt(oredChunk.getWidth());
            int y = 2 * random.nextInt(maxY) + (minY - maxY);
            int z = random.nextInt(oredChunk.getLength());

            if (this.lowestY <= y && y < this.lowestY + oredChunk.getHeight()) {
                generator.generateInto(oredChunk, random, x, y - this.lowestY, z);
            }
        }
    }
}
