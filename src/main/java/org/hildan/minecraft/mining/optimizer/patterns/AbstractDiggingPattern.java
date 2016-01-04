package org.hildan.minecraft.mining.optimizer.patterns;

import org.hildan.minecraft.mining.optimizer.chunks.Block;
import org.hildan.minecraft.mining.optimizer.chunks.Explorer;
import org.hildan.minecraft.mining.optimizer.chunks.Sample;
import org.hildan.minecraft.mining.optimizer.chunks.Wrapping;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This abstract class regroups common behavior for all patterns.
 */
public abstract class AbstractDiggingPattern implements DiggingPattern {

    @Override
    public void dig(Sample sample) {
        Collection<Access> accesses = new ArrayList<>();
        for (int x = 0; x < sample.getWidth(); x += getWidth()) {
            for (int y = 0; y < sample.getHeight(); y += getHeight()) {
                accesses.addAll(getAccesses(x, y));
                for (int z = 0; z < sample.getLength(); z += getLength()) {
                    digInto(sample, x, y, z);
                }
            }
        }
        Explorer.explore(sample, accesses);
        digVisibleOres(sample);
    }

    private static void digVisibleOres(Sample sample) {
        for (int y = 0; y < sample.getHeight(); y++) {
            for (int z = 0; z < sample.getLength(); z++) {
                for (int x = 0; x < sample.getWidth(); x++) {
                    Block block = sample.getBlock(x, y, z);
                    if (block.isOre() && block.isVisible()) {
                        digBlockAndAdjacentOres(sample, block);
                    }
                }
            }
        }
    }

    private static void digBlockAndAdjacentOres(Sample sample, Block block) {
        sample.dig(block.getX(), block.getY(), block.getZ());
        Collection<Block> adjacentBlocks = sample.getAdjacentBlocks(block, Wrapping.CUT);
        for (Block adjacentBlock : adjacentBlocks) {
            if (adjacentBlock.isOre() && adjacentBlock.isVisible()) {
                digBlockAndAdjacentOres(sample, adjacentBlock);
            }
        }
    }

    /**
     * Digs this pattern into the given sample, starting from the given origin, and going in the increasing direction of
     * each coordinate. This method takes care of stopping at the edge of the given sample.
     */
    protected abstract void digInto(Sample sample, int originX, int originY, int originZ);

    public String toString() {
        Sample sample = new Sample(getWidth(), getHeight(), getLength());
        dig(sample);
        return sample.toString();
    }
}
