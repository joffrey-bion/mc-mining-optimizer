package org.hildan.minecraft.mining.optimizer.patterns;

import org.hildan.minecraft.mining.optimizer.chunks.Block;
import org.hildan.minecraft.mining.optimizer.chunks.Chunk;
import org.hildan.minecraft.mining.optimizer.chunks.Wrapping;

import java.util.List;

/**
 * This abstract class regroups common behavior for all patterns.
 */
public abstract class AbstractDiggingPattern implements DiggingPattern {

    @Override
    public void dig(Chunk chunk) {
        for (int x = 0; x < chunk.getWidth(); x += getWidth()) {
            for (int y = 0; y < chunk.getHeight(); y += getHeight()) {
                for (int z = 0; z < chunk.getLength(); z += getLength()) {
                    digInto(chunk, x, y, z);
                }
            }
        }
        digVisibleOres(chunk);
    }

    private static void digVisibleOres(Chunk chunk) {
        for (int y = 0; y < chunk.getHeight(); y++) {
            for (int z = 0; z < chunk.getLength(); z++) {
                for (int x = 0; x < chunk.getWidth(); x++) {
                    Block block = chunk.getBlock(x, y, z);
                    if (block.isOre() && block.isVisible()) {
                        digBlockAndAdjacentOres(chunk, block);
                    }
                }
            }
        }
    }

    private static void digBlockAndAdjacentOres(Chunk chunk, Block block) {
        chunk.dig(block.getX(), block.getY(), block.getZ());
        List<Block> adjacentBlocks = chunk.getAdjacentBlocks(block, Wrapping.CUT);
        for (Block adjacentBlock : adjacentBlocks) {
            if (adjacentBlock.isOre() && adjacentBlock.isVisible()) {
                digBlockAndAdjacentOres(chunk, adjacentBlock);
            }
        }
    }

    /**
     * Digs this pattern into the given chunk, starting from the given origin, and going in the increasing direction of
     * each coordinate. This method takes care of stopping at the edge of the given chunk.
     */
    protected abstract void digInto(Chunk chunk, int originX, int originY, int originZ);

    public String toString() {
        Chunk chunk = new Chunk(getWidth(), getHeight(), getLength());
        dig(chunk);
        return chunk.toString();
    }
}
