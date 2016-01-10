package org.hildan.minecraft.mining.optimizer.patterns.generated.actions;

import org.hildan.minecraft.mining.optimizer.chunks.Block;
import org.hildan.minecraft.mining.optimizer.chunks.Sample;
import org.hildan.minecraft.mining.optimizer.chunks.Wrapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class DigAction implements Action {

    private static final int MAX_Y_DISTANCE = 3;

    private final int distanceX;

    private final int distanceY;

    private final int distanceZ;

    private DigAction(int distanceX, int distanceY, int distanceZ) {
        this.distanceX = distanceX;
        this.distanceY = distanceY;
        this.distanceZ = distanceZ;
    }

    public static Iterable<? extends Action> getAll() {
        return getAll(DigRange3D.STRICT);
    }

    public static Iterable<? extends Action> getAllWithShift() {
        return getAll(DigRange3D.PRESSING_SHIFT);
    }

    private static Iterable<? extends Action> getAll(DigRange3D range) {
        Collection<DigAction> moves = new ArrayList<>(12);
        final int maxY = range.maxY();
        for (int dY = -maxY; dY <= maxY; dY++) {
            final int maxX = range.maxX(dY);
            final int maxZ = range.maxZ(dY);
            for (int dX = -maxX; dX <= maxX; dX++) {
                for (int dZ = -maxZ; dZ <= maxZ; dZ++) {
                    if (dX == 0 && dZ == 0) {
                        continue; // never dig above or below
                    }
                    if (range.inRange(dX, dY, dZ)) {
                        moves.add(new DigAction(dX, dY, dZ));
                    }
                }
            }
        }
        return moves.stream().sorted((a1, a2) -> a1.norm() - a2.norm()).collect(Collectors.toList());
    }

    private int norm() {
        return distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ;
    }

    @Override
    public boolean isValidFor(Sample sample, Block currentHeadPosition) {
        Block blockToDig = sample.getBlock(currentHeadPosition, distanceX, distanceY, distanceZ, Wrapping.CUT);
        if (blockToDig == null || blockToDig.isDug()) {
            return false;
        }

        // TODO check that the view is not obstructed

        return true;
    }

    @Override
    public Block applyTo(Sample sample, Block currentHeadPosition) throws IllegalStateException {
        Block blockToDig = sample.getBlock(currentHeadPosition, distanceX, distanceY, distanceZ, Wrapping.CUT);
        sample.dig(blockToDig);
        // we haven't moved
        return currentHeadPosition;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        DigAction digAction = (DigAction) obj;

        if (distanceX != digAction.distanceX) {
            return false;
        }
        if (distanceY != digAction.distanceY) {
            return false;
        }
        return distanceZ == digAction.distanceZ;
    }

    @Override
    public int hashCode() {
        int result = distanceX;
        result = 31 * result + distanceY;
        result = 31 * result + distanceZ;
        return result;
    }

    @Override
    public String toString() {
        return String.format("Dig(%d,%d,%d)", distanceX, distanceY, distanceZ);
    }
}
