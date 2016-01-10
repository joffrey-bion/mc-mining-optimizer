package org.hildan.minecraft.mining.optimizer.patterns.generated.actions;

import org.hildan.minecraft.mining.optimizer.geometry.Range3D;

/**
 * Represents a 3D range of what can be dug, which is symmetric with respect to the XY, YZ, and XZ planes, and has
 * symmetric behaviours for X and Z (symmetric with respect to the 45°-diagonal vertical planes).
 */
public enum DigRange3D implements Range3D {

    PRESSING_SHIFT(new int[][]{ //
            {6, 6, 6, 5, 5, 4, 2}, // distanceY = 0
            {6, 6, 6, 5, 5, 4, 2}, // distanceY = 1 or -1
            {6, 6, 5, 5, 5, 4, 1}, // distanceY = 2 or -2
            {5, 5, 5, 5, 4, 3, -1} // distanceY = 3 or -3
    }),
    STRICT(new int[][]{ //
            {5, 5, 5, 5, 4, 3}, // distanceY = 0
            {5, 5, 5, 5, 4, 3}, // distanceY = 1 or -1
            {5, 5, 5, 4, 3, 2}, // distanceY = 2 or -2
            {4, 4, 4, 4, 3, -1} // distanceY = 3 or -3
    });

    /**
     * {@code boundsDistribution[y][x]} gives the maximum that Z can take for the given X and Y.
     */
    private final int[][] boundsDistribution;

    DigRange3D(int[][] boundsDistribution) {
        this.boundsDistribution = boundsDistribution;
    }

    @Override
    public int maxX(int distanceY) {
        return boundsDistribution[Math.abs(distanceY)].length - 1;
    }

    @Override
    public int maxY() {
        return boundsDistribution.length - 1;
    }

    @Override
    public boolean inRange(int distanceX, int distanceY, int distanceZ) {
        if (distanceY < minY() || distanceY > maxY()) {
            return false;
        }
        if (distanceX < minX(distanceY) || distanceX > maxX(distanceY)) {
            return false;
        }
        return Math.abs(distanceZ) <= boundsDistribution[Math.abs(distanceY)][Math.abs(distanceX)];
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int dY = 0; dY <= maxY(); dY++) {
            for (int dX = minX(dY); dX <= maxX(dY); dX++) {
                for (int dZ = minZ(dY); dZ <= maxZ(dY); dZ++) {
                    if (inRange(dX, dY, dZ)) {
                        sb.append('.');
                    } else {
                        sb.append('X');
                    }
                }
                sb.append(String.format("%n"));
            }
            sb.append(String.format("%n"));
        }
        return sb.toString();
    }
}
