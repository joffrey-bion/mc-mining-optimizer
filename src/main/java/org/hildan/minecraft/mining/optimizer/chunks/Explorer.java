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
        block.setVisible(block.isDug() || adjBlocks.stream().anyMatch(Block::isDug));

        if (block.isDug()) {
            setAccessibility(sample, block);
            adjBlocks.forEach(blocksToExplore::addLast);
        }
        block.setExplored(true);
    }

    private static void setAccessibility(Sample sample, Block dugBlock) {
        assert dugBlock.isDug() : "the given block must be dug already";
        Block above = sample.getBlockAbove(dugBlock, Wrapping.WRAP);
        if (above.isExplored() && above.isHeadAccessible()) {
            dugBlock.setFeetAccessible(true);
            return;
        }
        Block below = sample.getBlockBelow(dugBlock, Wrapping.WRAP);
        if (below.isExplored() && below.isFeetAccessible()) {
            dugBlock.setHeadAccessible(true);
            return;
        }
        if (below.isDug()) {
            Block belowBelow = sample.getBlockBelow(below, Wrapping.WRAP);
            if (!belowBelow.isDug()) {
                // potential head accessible
                dugBlock.setHeadAccessible(isAccessible(sample, below, dugBlock, above));
            }
        } else {
            if (above.isDug()) {
                Block aboveAbove = sample.getBlockAbove(above, Wrapping.WRAP);
                // potential feet accessible
                dugBlock.setFeetAccessible(isAccessible(sample, dugBlock, above, aboveAbove));
            }
        }
    }

    private static boolean isAccessible(Sample sample, Block feet, Block head, Block aboveHead) {
        Collection<Block> exploredHeadNeighbors = sample.getHorizontallyAdjacentBlocks(head, Wrapping.WRAP);
        exploredHeadNeighbors.removeIf(b -> !b.isExplored());

        // can walk to it
        if (exploredHeadNeighbors.stream().anyMatch(Block::isHeadAccessible)) {
            return true;
        }

        // can jump down to it
        if (aboveHead.isDug() && exploredHeadNeighbors.stream().anyMatch(Block::isFeetAccessible)) {
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
}
