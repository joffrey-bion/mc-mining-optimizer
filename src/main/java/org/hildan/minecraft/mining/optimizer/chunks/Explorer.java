package org.hildan.minecraft.mining.optimizer.chunks;

import org.hildan.minecraft.mining.optimizer.patterns.Access;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

/**
 * A tool explore and validate a sample.
 */
public class Explorer {

    /**
     * Explores the given sample to update the visibility and accessibility of its blocks.
     *
     * @param sample
     *         the sample to explore
     * @param accesses
     *         the accesses to start the exploration from
     */
    public static void explore(Sample sample, Iterable<Access> accesses) {
        for (Access access : accesses) {
            exploreAccess(sample, access);
        }
    }

    private static void exploreAccess(Sample sample, Access access) {
        if (!sample.hasBlock(access.getX(), access.getY(), access.getZ())) {
            return;
        }
        Block feetBlock = sample.getBlock(access);
        assert feetBlock.isDug() : "the given sample's access has not been dug at feet level";
        feetBlock.setFeetAccessible(true);

        Block headBlock = sample.getBlockAbove(feetBlock, Wrapping.WRAP);
        assert headBlock.isDug() : "the given sample's access has not been dug at head level";
        headBlock.setHeadAccessible(true);

        Deque<Block> blocksToExplore =
                new ArrayDeque<>(sample.getWidth() * sample.getHeight() * sample.getLength() / 3);
        blocksToExplore.addLast(headBlock);
        while (!blocksToExplore.isEmpty()) {
            Block block = blocksToExplore.pollFirst();
            if (!block.isExplored()) {
                exploreBlock(sample, block, blocksToExplore);
            }
        }
    }

    private static void exploreBlock(Sample sample, Block block, Deque<Block> blocksToExplore) {
        Collection<Block> adjBlocks = sample.getAdjacentBlocks(block, Wrapping.WRAP);

        // FIXME add proper visibility algorithm based on accessible blocks
        boolean hasDugNeighbor = adjBlocks.stream().anyMatch(Block::isDug);
        block.setVisible(block.isDug() || hasDugNeighbor);

        if (block.isDug()) {
            setAccessibility(sample, block);
            adjBlocks.forEach(blocksToExplore::addLast);
        }
        block.setExplored(true);
    }

    private static void setAccessibility(Sample sample, Block block) {
        if (!block.isDug()) {
            block.setHeadAccessible(false);
            block.setFeetAccessible(false);
            return;
        }
        Block above = sample.getBlockAbove(block, Wrapping.WRAP);
        if (above.isExplored() && above.isHeadAccessible()) {
            block.setFeetAccessible(true);
            return;
        }
        Block below = sample.getBlockBelow(block, Wrapping.WRAP);
        if (below.isExplored() && below.isFeetAccessible()) {
            block.setHeadAccessible(true);
            return;
        }
        if (below.isDug()) {
            Block belowBelow = sample.getBlockBelow(below, Wrapping.WRAP);
            if (!belowBelow.isDug()) {
                // potential head accessible
                block.setHeadAccessible(isAccessible(sample, below, block, above));
            }
        } else {
            if (above.isDug()) {
                Block aboveAbove = sample.getBlockAbove(above, Wrapping.WRAP);
                // potential feet accessible
                block.setFeetAccessible(isAccessible(sample, block, above, aboveAbove));
            }
        }
    }

    private static boolean isAccessible(Sample sample, Block feet, Block head, Block aboveHead) {
        // can walk to it
        boolean directlyAccessible = hasHeadAccessibleHorizontalNeighbor(sample, head);
        if (directlyAccessible) {
            return true;
        }

        // can jump down to it
        boolean canStandNextToIt = hasFeetAccessibleHorizontalNeighbor(sample, head);
        if (canStandNextToIt && aboveHead.isDug()) {
            return true;
        }

        // can jump up to it
        return sample.getHorizontallyAdjacentBlocks(feet, Wrapping.WRAP)
                     .stream()
                     .filter(Block::isExplored)
                     .filter(Block::isHeadAccessible)
                     .map(b -> sample.getBlockAbove(b, Wrapping.WRAP))
                     .anyMatch(Block::isDug);
    }

    private static boolean hasHeadAccessibleHorizontalNeighbor(Sample sample, Block block) {
        return sample.getHorizontallyAdjacentBlocks(block, Wrapping.WRAP)
                     .stream()
                     .filter(Block::isExplored)
                     .anyMatch(Block::isHeadAccessible);
    }

    private static boolean hasFeetAccessibleHorizontalNeighbor(Sample sample, Block block) {
        return sample.getHorizontallyAdjacentBlocks(block, Wrapping.WRAP)
                     .stream()
                     .filter(Block::isExplored)
                     .anyMatch(Block::isFeetAccessible);
    }

    /**
     * Tests whether the given sample could actually have the current state in Minecraft. This includes testing whether
     * blocks could have been dug this way.
     *
     * @param sample
     *         the sample to test
     * @return true if the sample is valid
     */
    public static boolean isValid(Sample sample) {

        // TODO check whether the dug blocks are arranged in a way that could indeed have been dug

        return true;
    }
}
