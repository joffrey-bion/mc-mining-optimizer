package org.hildan.minecraft.mining.optimizer.patterns;

import org.hildan.minecraft.mining.optimizer.chunks.Chunk;

public abstract class AbstractDiggingPattern implements DiggingPattern {

    @Override
    public Chunk dig(Chunk oredChunk) {
        Chunk dugChunk = new Chunk(oredChunk);
        for (int x = 0; x < dugChunk.getWidth(); x += getWidth()) {
            for (int y = 0; y < dugChunk.getHeight(); y += getHeight()) {
                for (int z = 0; z < dugChunk.getLength(); z += getLength()) {
                    digInto(dugChunk, x, y, z);
                }
            }
        }
        digVisibleOres(dugChunk);
        return dugChunk;
    }

    private void digVisibleOres(Chunk dugChunk) {

        // TODO follow ores and dig all the visible ones like a human would do (and iterate)

    }

    /**
     * Digs this pattern into the given chunk, starting from the given origin, and going in the increasing direction of each coordinate.
     * This method takes care of stopping at the edge of the given chunk.
     */
    protected abstract void digInto(Chunk chunk, int originX, int originY, int originZ);
}
