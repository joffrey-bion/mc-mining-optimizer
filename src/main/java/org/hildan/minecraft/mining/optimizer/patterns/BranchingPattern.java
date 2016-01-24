package org.hildan.minecraft.mining.optimizer.patterns;

import org.hildan.minecraft.mining.optimizer.blocks.Sample;
import org.hildan.minecraft.mining.optimizer.geometry.Axis;
import org.hildan.minecraft.mining.optimizer.patterns.tunnels.TunnelPattern;

import java.util.HashSet;
import java.util.Set;

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
            throw new IllegalArgumentException(
                    "Branches from 2 different shafts are touching: reduce branch length, or put more space");
        }
    }

    @Override
    public int getWidth() {
        // the offset doesn't matter here, the spatial period is the same
        return 2 * branchLength + shaft.getShape().getWidth();
    }

    private int getLayerHeight() {
        return shaft.getShape().getHeight() + shaft.getVSpacing();
    }

    @Override
    public int getHeight() {
        // with an offset, two consecutive layers are different
        return getLayerHeight() * 2;
    }

    @Override
    public int getLength() {
        return branch.getHSpacing() + branch.getShape().getWidth();
    }

    @Override
    public Set<Access> getAccesses(int x, int y) {
        Set<Access> accesses = new HashSet<>();
        accesses.add(new Access(x + branchLength, y));
        accesses.add(new Access(x + branchLength, y + getLayerHeight()));
        return accesses;
    }

    @Override
    public void digInto(Sample sample, int originX, int originY, int originZ) {
        digLayer(sample, originX, originY, originZ, 0);
        digLayer(sample, originX, originY + getLayerHeight(), originZ, branchOffsetByTier);
    }

    private void digLayer(Sample sample, int originX, int originY, int originZ, int offset) {
        branch.digInto(sample, originX, originY, originZ + offset, branchLength, Axis.X, Axis.Y);
        shaft.digInto(sample, originX + branchLength, originY, originZ, getLength(), Axis.Z, Axis.Y);
        int oppositeBranchStartX = originX + branchLength + shaft.getShape().getWidth();
        branch.digInto(sample, oppositeBranchStartX, originY, originZ + offset, branchLength, Axis.X, Axis.Y);
    }
}
