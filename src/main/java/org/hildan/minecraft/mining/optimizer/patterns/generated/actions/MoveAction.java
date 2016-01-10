package org.hildan.minecraft.mining.optimizer.patterns.generated.actions;

import org.hildan.minecraft.mining.optimizer.chunks.Block;
import org.hildan.minecraft.mining.optimizer.chunks.Sample;
import org.hildan.minecraft.mining.optimizer.chunks.Wrapping;

import java.util.ArrayList;
import java.util.Collection;

/**
 * An immutable action representing the player moving of one block horizontally. The move can be done in any 4
 * horizontal directions, and can result in the player going up or down one block as well.
 */
public class MoveAction implements Action {

    private final int distanceX;

    private final int distanceY;

    private final int distanceZ;

    private MoveAction(int distanceX, int distanceY, int distanceZ) {
        if (distanceY > 1) {
            throw new IllegalArgumentException("Can't jump higher than 1 block");
        }
        if (distanceY < -1) {
            throw new IllegalArgumentException("No going down lower than 1 block, to be able to go back");
        }
        if (distanceX == 0 && distanceZ == 0) {
            throw new IllegalArgumentException("Cannot stay in the same horizontal place, falls are not actions");
        }
        if (distanceX != 0 && distanceZ != 0) {
            throw new IllegalArgumentException("Moves are accepted only along one axis at a time");
        }
        if (Math.abs(distanceX) > 1 || Math.abs(distanceZ) > 1) {
            throw new IllegalArgumentException("Only moves of one block are accepted");
        }
        this.distanceX = distanceX;
        this.distanceY = distanceY;
        this.distanceZ = distanceZ;
    }

    public static Collection<? extends Action> getAll() {
        final int[] values = {0, 1, -1};
        Collection<MoveAction> moves = new ArrayList<>(12);
        for (int y : values) {
            for (int x : values) {
                for (int z : values) {
                    if (x == 0 && z == 0) {
                        continue; // we have to move horizontally
                    }
                    if (x != 0 && z != 0) {
                        continue; // we can't move more than a block away
                    }
                    moves.add(new MoveAction(x, y, z));
                }
            }
        }
        return moves;
    }

    @Override
    public boolean isValidFor(Sample sample, Block currentHeadPosition) {
        // check that there is room for the head
        Block headDestination = sample.getBlock(currentHeadPosition, distanceX, distanceY, distanceZ, Wrapping.CUT);
        if (headDestination == null || !headDestination.isDug()) {
            return false;
        }
        // check that there is room for the feet
        Block feetDestination = sample.getBlockBelow(headDestination, Wrapping.CUT);
        if (feetDestination == null || !feetDestination.isDug()) {
            return false;
        }
        // check that there is room for the movement
        return hasRoomForMovement(sample, currentHeadPosition, headDestination);
    }

    /**
     * Checks whether the intermediate position of the player during the movement is clear.
     *
     * @param sample
     *         the current sample
     * @param headPositionBefore
     *         the position of the head before the movement
     * @param headPositionAfter
     *         the position of the head after the movement
     * @return true if the intermediate block is clear
     */
    private boolean hasRoomForMovement(Sample sample, Block headPositionBefore, Block headPositionAfter) {
        switch (distanceY) {
            case 0:
                return true;
            case 1:
                Block jumpRoom = sample.getBlockAbove(headPositionBefore, Wrapping.CUT);
                return jumpRoom != null && jumpRoom.isDug();
            case -1:
                Block forwardRoom = sample.getBlockAbove(headPositionAfter, Wrapping.CUT);
                return forwardRoom != null && forwardRoom.isDug();
            default:
                // can't jump higher than 1
                // (counts also for the negative Ys because we want to be able to go back)
                return false;
        }
    }

    @Override
    public Block executeOn(Sample sample, Block currentHeadPosition) {
        return sample.getBlock(currentHeadPosition, distanceX, distanceY, distanceZ, Wrapping.CUT);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        MoveAction moveAction = (MoveAction) obj;

        if (distanceX != moveAction.distanceX) {
            return false;
        }
        if (distanceY != moveAction.distanceY) {
            return false;
        }
        return distanceZ == moveAction.distanceZ;
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
        return String.format("MoveOf(%d,%d,%d)", distanceX, distanceY, distanceZ);
    }
}
