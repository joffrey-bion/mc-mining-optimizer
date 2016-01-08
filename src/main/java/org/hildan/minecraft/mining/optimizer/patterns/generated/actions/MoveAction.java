package org.hildan.minecraft.mining.optimizer.patterns.generated.actions;

import org.hildan.minecraft.mining.optimizer.chunks.Block;
import org.hildan.minecraft.mining.optimizer.chunks.Sample;
import org.hildan.minecraft.mining.optimizer.chunks.Wrapping;

import java.util.ArrayList;
import java.util.Collection;

public class MoveAction implements Action {

    private final int distanceX;

    private final int distanceY;

    private final int distanceZ;

    private MoveAction(int distanceX, int distanceY, int distanceZ) {
        this.distanceX = distanceX;
        this.distanceY = distanceY;
        this.distanceZ = distanceZ;
    }

    public static Collection<Action> getAllMoves() {
        final int[] values = {-1,0,1};
        Collection<Action> moves = new ArrayList<>(12);
        for (int x : values) {
            for (int y : values) {
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
        Block headDestination = sample.getBlock(currentHeadPosition, distanceX, distanceY, distanceZ, Wrapping.CUT);
        if (headDestination == null || !headDestination.isDug()) {
            return false;
        }
        Block feetDestination = sample.getBlockBelow(headDestination, Wrapping.CUT);
        return feetDestination != null && feetDestination.isDug();
    }

    @Override
    public Block applyTo(Sample sample, Block currentHeadPosition) {
        return sample.getBlock(currentHeadPosition, distanceX, distanceY, distanceZ, Wrapping.CUT);
    }
}
