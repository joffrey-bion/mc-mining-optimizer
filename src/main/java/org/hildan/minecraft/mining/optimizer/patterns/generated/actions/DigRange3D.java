package org.hildan.minecraft.mining.optimizer.patterns.generated.actions;

/**
 * Represents a 3D range of what can be dug, which is symmetric with respect to the XY, YZ, and XZ planes.
 */
public enum DigRange3D {

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

    private final int[][] boundsDistribution;

    DigRange3D(int[][] boundsDistribution) {
        this.boundsDistribution = boundsDistribution;
    }

    public int maxX(int distanceY) {
        return boundsDistribution[Math.abs(distanceY)].length - 1;
    }

    public int maxY() {
        return boundsDistribution.length - 1;
    }

    public int maxZ(int distanceY) {
        return maxX(distanceY); // symmetric X and Z variations
    }

    public boolean inRange(int distanceX, int distanceY, int distanceZ) {
        return Math.abs(distanceZ) <= boundsDistribution[Math.abs(distanceY)][Math.abs(distanceX)];
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        final int maxY = maxY();
        for (int dY = 0; dY <= maxY; dY++) {
            final int maxX = maxX(dY);
            final int maxZ = maxZ(dY);
            for (int dX = -maxX; dX <= maxX; dX++) {
                for (int dZ = -maxZ; dZ <= maxZ; dZ++) {
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
