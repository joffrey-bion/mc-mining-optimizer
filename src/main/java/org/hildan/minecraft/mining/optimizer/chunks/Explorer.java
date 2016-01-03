package org.hildan.minecraft.mining.optimizer.chunks;

import org.hildan.minecraft.mining.optimizer.patterns.Access;

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
        Block block = sample.getBlock(access);
        if (!block.isDug()) {
            throw new IllegalStateException("the given sample's access has not been dug");
        }
        block.setVisible(true);

        // TODO
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
