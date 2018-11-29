package org.hildan.minecraft.mining.optimizer.blocks

import org.hildan.minecraft.mining.optimizer.patterns.Access

import java.util.ArrayDeque
import java.util.Deque

/**
 * Explores the given [this@explore] from the given [accesses] to update the visibility and accessibility of its blocks.
 */
fun Sample.explore(accesses: Iterable<Access>) {
    accesses.forEach { exploreAccess(it) }
}

private fun Sample.exploreAccess(access: Access) {
    if (!hasBlock(access.feet) || !hasBlock(access.head)) {
        return
    }
    val feetBlock = getBlock(access.feet)
    assert(feetBlock.isDug) { "the given sample's access has not been dug at feet level" }

    val headBlock = getBlock(access.head)
    assert(headBlock.isDug) { "the given sample's access has not been dug at head level" }

    val blocksToExplore = ArrayDeque<Block>().apply { addLast(headBlock) }

    while (!blocksToExplore.isEmpty()) {
        val block = blocksToExplore.pollFirst()
        if (!block.isExplored) {
            exploreBlock(block, blocksToExplore)
        }
    }
}

private fun Sample.exploreBlock(block: Block, blocksToExplore: Deque<Block>) {
    val adjBlocks = getAdjacentBlocks(block, Wrapping.WRAP)

    // FIXME add proper visibility algorithm based on accessible blocks
    block.isVisible = block.isDug || adjBlocks.any { it.isDug }

    if (block.isDug) {
        adjBlocks.forEach { blocksToExplore.addLast(it) }
    }
    block.isExplored = true
}
