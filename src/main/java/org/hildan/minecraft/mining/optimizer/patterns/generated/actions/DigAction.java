package org.hildan.minecraft.mining.optimizer.patterns.generated.actions;

import org.hildan.minecraft.mining.optimizer.chunks.Block;
import org.hildan.minecraft.mining.optimizer.chunks.Sample;
import org.hildan.minecraft.mining.optimizer.chunks.Wrapping;
import org.hildan.minecraft.mining.optimizer.geometry.Position;
import org.hildan.minecraft.mining.optimizer.geometry.Range3D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * An immutable action representing the player digging one block in an acceptable range. Digging above or below the
 * player is forbidden (we don't want to fall in a cave, or be covered in lava).
 */
public class DigAction implements Action {

    private final int distanceX;

    private final int distanceY;

    private final int distanceZ;

    private DigAction(int distanceX, int distanceY, int distanceZ) {
        if (distanceX == 0 && distanceZ == 0) {
            throw new IllegalArgumentException("Never dig above the head or below the feet");
        }
        this.distanceX = distanceX;
        this.distanceY = distanceY;
        this.distanceZ = distanceZ;
    }

    /**
     * Gets all the possible digging actions for the given accepted range.
     *
     * @param range
     *         the digging range of the player
     * @return a collection of actions that can potentially be done
     */
    public static Collection<? extends Action> getAll(Range3D range) {
        Collection<DigAction> moves = new ArrayList<>(12);
        for (int dY = range.minY(); dY <= range.maxY(); dY++) {
            for (int dX = range.minX(dY); dX <= range.maxX(dY); dX++) {
                for (int dZ = range.minZ(dY); dZ <= range.maxZ(dY); dZ++) {
                    if (dX == 0 && dZ == 0) {
                        continue; // never dig above the head or below the feet
                    }
                    if (range.inRange(dX, dY, dZ)) {
                        moves.add(new DigAction(dX, dY, dZ));
                    }
                }
            }
        }
        return moves.stream().sorted((a1, a2) -> a1.norm() - a2.norm()).collect(Collectors.toList());
    }

    @Override
    public boolean affectsSample() {
        return true;
    }

    /**
     * Returns the squared distance of the block to dig.
     *
     * @return the squared distance of the block to dig.
     */
    private int norm() {
        return distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ;
    }

    @Override
    public boolean isValidFor(Sample sample, Position currentHeadPosition) {
        Block blockToDig = sample.getBlock(currentHeadPosition, distanceX, distanceY, distanceZ, Wrapping.CUT);
        if (blockToDig == null || blockToDig.isDug()) {
            return false;
        }
        return isPathClear(sample, currentHeadPosition, blockToDig);
    }

    private boolean isPathClear(Sample sample, Position head, Position block) {
        int norm = norm();
        if (norm == 1 || (norm == 2 && distanceY == -1)) {
            return true;
        }

        // TODO implement true algorithm to check that the view is not obstructed

        return false;
    }

    @Override
    public Position executeOn(Sample sample, Position currentHeadPosition) throws IllegalStateException {
        Block blockToDig = sample.getBlock(currentHeadPosition, distanceX, distanceY, distanceZ, Wrapping.CUT);
        sample.digBlock(blockToDig);
        // we haven't moved
        return currentHeadPosition;
    }

    @Override
    public boolean isInverseOf(Action action) {
        // can't inverse a dig action
        return false;
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
