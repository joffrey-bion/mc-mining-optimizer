package org.hildan.minecraft.mining.optimizer.ore;

import org.hildan.minecraft.mining.optimizer.chunks.Chunk;
import org.hildan.minecraft.mining.optimizer.ore.WorldGenMinable;

import java.util.Random;

public class OreGenerator {

    /**
     * Generates a copy of the given chunk where some stones are replaced by ores. Only stone blocks may be changed.
     *
     * @param baseChunk
     *     the chunk to start from
     *
     * @return a new chunk based on the given one, containing ores instead of some of the stones.
     */
    public Chunk generate(Chunk baseChunk) {
        Random random = new Random();
        Chunk oredChunk = new Chunk(baseChunk);

        WorldGenMinable.coalGen.generateInto(oredChunk, random, 5, 5, 5);

        return oredChunk;
    }
}
