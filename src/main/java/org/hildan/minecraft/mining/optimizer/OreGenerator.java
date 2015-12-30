package org.hildan.minecraft.mining.optimizer;

import org.hildan.minecraft.mining.optimizer.chunks.Chunk;
import org.hildan.minecraft.mining.optimizer.chunks.OredChunk;

public class OreGenerator {

    /**
     * Generates a copy of the given chunk where some stones are replaced by ores. Only stone blocks may be changed.
     *
     * @param baseChunk
     *     the chunk to start from
     *
     * @return a new chunk based on the given one, containing ores instead of some of the stones.
     */
    public OredChunk generate(Chunk baseChunk) {
        OredChunk oredChunk = new OredChunk(baseChunk);

        // TODO implement true ore generation
        oredChunk.setOre(4, 4, 4);
        oredChunk.setOre(4, 4, 5);
        oredChunk.setOre(4, 4, 6);
        oredChunk.setOre(4, 5, 4);
        oredChunk.setOre(4, 5, 5);
        oredChunk.setOre(4, 5, 6);

        return oredChunk;
    }

}
