package org.hildan.minecraft.mining.optimizer.patterns;

import org.hildan.minecraft.mining.optimizer.chunks.DugChunk;
import org.hildan.minecraft.mining.optimizer.chunks.OredChunk;
import org.hildan.minecraft.mining.optimizer.patterns.tunnels.TunnelShape;

/**
 * One main shaft with perpendicular branches.
 */
public class BranchingPattern implements Pattern {

    private TunnelShape shaft;

    private TunnelShape branch;

    private int mainShaftSpacing;

    private int mainShaftLength;

    private int branchSpacing;

    private int branchLength;

    private int branchOffsetByTier;

    private int tierSpacing;

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public int[][] getAccesses() {

        // TODO calculate position of main shaft access(es) based on branch length and shaft/tier spacing

        return new int[0][];
    }

    @Override
    public void digInto(DugChunk chunk, int originX, int originY, int originZ) {

        // TODO dig the pattern into the chunk
    }
}
