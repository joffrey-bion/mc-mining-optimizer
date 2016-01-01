package org.hildan.minecraft.mining.optimizer.patterns;

import org.hildan.minecraft.mining.optimizer.chunks.Block;
import org.hildan.minecraft.mining.optimizer.chunks.Chunk;
import org.hildan.minecraft.mining.optimizer.geometry.Position;

import java.util.List;

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
        for (int y = 0; y < dugChunk.getHeight(); y++) {
            for (int z = 0; z < dugChunk.getLength(); z++) {
                for (int x = 0; x < dugChunk.getWidth(); x++) {
                    Block block = dugChunk.getBlock(x, y, z);
                    if (block != Block.AIR && block.isOre() && dugChunk.isDiscovered(x, y, z)) {
                        digOresAround(dugChunk, x, y, z);
                    }
                }
            }
        }
    }

    private void digOresAround(Chunk chunk, int originX, int originY, int originZ) {
        chunk.dig(originX, originY, originZ);
        List<Position> adjacentBlocks = chunk.getAdjacentBlocksInChunk(originX, originY, originZ);
        for (Position p : adjacentBlocks) {
            Block block = chunk.getBlock(p.getX(), p.getY(), p.getZ());
            if (block != Block.AIR && block.isOre() && chunk.isDiscovered(p.getX(), p.getY(), p.getZ())) {
                digOresAround(chunk, p.getX(), p.getY(), p.getZ());
            }
        }
    }

    /**
     * Digs this pattern into the given chunk, starting from the given origin, and going in the increasing direction of each coordinate.
     * This method takes care of stopping at the edge of the given chunk.
     */
    protected abstract void digInto(Chunk chunk, int originX, int originY, int originZ);

    public String toString() {
        Chunk chunk = new Chunk(getWidth(), getHeight(), getLength());
        chunk = dig(chunk);
        return chunk.toString();
    }
}
