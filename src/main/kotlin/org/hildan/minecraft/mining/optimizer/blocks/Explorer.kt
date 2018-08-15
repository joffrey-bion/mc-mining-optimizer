package org.hildan.minecraft.mining.optimizer.blocks

import org.hildan.minecraft.mining.optimizer.patterns.Access

import java.util.ArrayDeque
import java.util.Deque

/**
 * A tool explore and validate a sample.
 */
object Explorer {

    /**
     * Explores the given sample to update the visibility and accessibility of its blocks.
     *
     * @param sample
     * the sample to explore
     * @param accesses
     * the accesses to start the exploration from
     */
    fun explore(sample: Sample, accesses: Iterable<Access>) {
        for (access in accesses) {
            exploreAccess(sample, access)
        }
    }

    private fun exploreAccess(sample: Sample, access: Access) {
        if (!sample.hasBlock(access.feet) || !sample.hasBlock(access.head)) {
            return
        }
        val feetBlock = sample.getBlock(access.feet)
        assert(feetBlock.isDug) { "the given sample's access has not been dug at feet level" }

        val headBlock = sample.getBlock(access.head)
        assert(headBlock.isDug) { "the given sample's access has not been dug at head level" }

        val blocksToExplore = ArrayDeque<Block>(sample.width * sample.height * sample.length / 3)
        blocksToExplore.addLast(headBlock)
        while (!blocksToExplore.isEmpty()) {
            val block = blocksToExplore.pollFirst()
            if (!block.isExplored) {
                exploreBlock(sample, block, blocksToExplore)
            }
        }
    }

    private fun exploreBlock(sample: Sample, block: Block, blocksToExplore: Deque<Block>) {
        val adjBlocks = sample.getAdjacentBlocks(block, Wrapping.WRAP)

        // FIXME add proper visibility algorithm based on accessible blocks
        block.isVisible = block.isDug || adjBlocks.any { it.isDug }

        if (block.isDug) {
            adjBlocks.forEach { blocksToExplore.addLast(it) }
        }
        block.isExplored = true
    }
}
