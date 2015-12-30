package org.hildan.minecraft.mining.optimizer.patterns;

import org.hildan.minecraft.mining.optimizer.chunks.Chunk;
import org.hildan.minecraft.mining.optimizer.patterns.tunnels.TunnelPattern;

/**
 * One main shaft with perpendicular branches.
 */
public class BranchingPattern extends AbstractDiggingPattern {

    private final TunnelPattern shaft;

    private final TunnelPattern branch;

    private final int branchLength;

    private final int branchOffsetByTier;

    public BranchingPattern(TunnelPattern shaft, TunnelPattern branch, int branchLength, int branchOffsetByTier) {
        this.shaft = shaft;
        this.branch = branch;
        this.branchLength = branchLength;
        this.branchOffsetByTier = branchOffsetByTier;
        if (shaft.getShape().getHeight() < branch.getShape().getHeight()) {
            throw new IllegalArgumentException("The main shaft should be higher than branches");
        }
        if (shaft.getHSpacing() < 2 * branch.getShape().getWidth()) {
            throw new IllegalArgumentException("Branches from 2 different shafts are touching: reduce branch length, or put more space");
        }
    }

    public int getWidth() {
        // the offset doesn't matter here, the spatial period is the same
        return 2 * branchLength + shaft.getShape().getWidth();
    }

    public int getHeight() {
        int layerHeight = shaft.getShape().getHeight() + shaft.getVSpacing();
        // with an offset, two consecutive layers are different
        return branchOffsetByTier == 0 ? layerHeight : layerHeight * 2;
    }

    public int getLength() {
        return branch.getHSpacing() + branch.getShape().getWidth();
    }

    @Override
    public int[][] getAccesses() {

        // TODO calculate position of main shaft access(es) based on branch length and shaft/tier spacing

        return new int[0][];
    }

    @Override
    public void digInto(Chunk chunk, int originX, int originY, int originZ) {

        // TODO dig the pattern into the chunk
    }
}
