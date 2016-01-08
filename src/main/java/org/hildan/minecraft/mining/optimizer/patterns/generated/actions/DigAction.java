package org.hildan.minecraft.mining.optimizer.patterns.generated.actions;

import org.hildan.minecraft.mining.optimizer.chunks.Block;
import org.hildan.minecraft.mining.optimizer.chunks.Sample;
import org.hildan.minecraft.mining.optimizer.chunks.Wrapping;

import java.util.ArrayList;
import java.util.Collection;

public class DigAction implements Action {

    private static final int MAX_Y_DISTANCE = 3;

    private final int distanceX;

    private final int distanceY;

    private final int distanceZ;

    DigAction(int distanceX, int distanceY, int distanceZ) {
        this.distanceX = distanceX;
        this.distanceY = distanceY;
        this.distanceZ = distanceZ;
    }

    private static final int[][] boundWithShift =
            {{6, 6, 6, 5, 5, 4, 2}, {6, 6, 6, 5, 5, 4, 2}, {6, 6, 5, 5, 5, 4, 1}, {5, 5, 5, 5, 4, 3, -1}};

    private static final int[][] boundStrict =
            {{5, 5, 5, 5, 4, 3}, {5, 5, 5, 5, 4, 3}, {5, 5, 5, 4, 3, 2}, {4, 4, 4, 4, 3, -1}};

    private static Collection<Action> getAll(int[][] boundDistribution) {
        Collection<Action> moves = new ArrayList<>(12);
        for (int y = -MAX_Y_DISTANCE; y <= MAX_Y_DISTANCE; y++) {
            final int max = boundDistribution[y].length;
            for (int x = -max; x <= max; x++) {
                for (int z = -max; z <= max; z++) {
                    if (x == 0 && y == 0 && z == 0) {
                        continue; // skip origin
                    }
                    if (Math.abs(x) > boundDistribution[Math.abs(y)][Math.abs(z)]) {
                        continue;
                    }
                    if (Math.abs(z) > boundDistribution[Math.abs(y)][Math.abs(x)]) {
                        continue;
                    }
                    moves.add(new DigAction(x, y, z));
                }
            }
        }
        return moves;
    }

    public static Collection<Action> getAll() {
        return getAll(boundStrict);
    }

    public static Collection<Action> getAllWithShift() {
        return getAll(boundWithShift);
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
}
