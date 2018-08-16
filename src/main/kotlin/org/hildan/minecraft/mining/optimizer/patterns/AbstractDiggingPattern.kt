package org.hildan.minecraft.mining.optimizer.patterns

import org.hildan.minecraft.mining.optimizer.blocks.Block
import org.hildan.minecraft.mining.optimizer.blocks.BlockType
import org.hildan.minecraft.mining.optimizer.blocks.Explorer
import org.hildan.minecraft.mining.optimizer.blocks.Sample
import org.hildan.minecraft.mining.optimizer.blocks.Wrapping

/**
 * This abstract class regroups common behavior for all patterns.
 */
abstract class AbstractDiggingPattern : DiggingPattern {

    override fun digInto(sample: Sample) {
        val accesses = mutableListOf<Access>()
        for (x in 0 until sample.width step width) {
            for (y in 0 until sample.height step height) {
                accesses.addAll(getAccesses(x, y))
                for (z in 0 until sample.length step length) {
                    digInto(sample, x, y, z)
                }
            }
        }
        Explorer.explore(sample, accesses)
        digVisibleOres(sample)
    }

    private fun digVisibleOres(sample: Sample) {
        sample.getBlocksMatching { it.isOre && it.isVisible }.forEach { digBlockAndAdjacentOres(sample, it) }
    }

    private fun digBlockAndAdjacentOres(sample: Sample, block: Block) {
        sample.digBlock(block.x, block.y, block.z)
        val adjacentBlocks = sample.getAdjacentBlocks(block, Wrapping.CUT)
        for (adjacentBlock in adjacentBlocks) {
            if (adjacentBlock.isOre && adjacentBlock.isVisible) {
                digBlockAndAdjacentOres(sample, adjacentBlock)
            }
        }
    }

    /**
     * Digs this pattern into the given sample, starting from the given origin, and going in the increasing direction of
     * each coordinate. This method must take care of stopping at the edge of the given sample.
     */
    protected abstract fun digInto(sample: Sample, originX: Int, originY: Int, originZ: Int)

    override fun toString(): String {
        val sample = Sample(width, height, length, BlockType.STONE)
        digInto(sample)
        return sample.toString()
    }
}
