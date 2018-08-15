package org.hildan.minecraft.mining.optimizer.patterns.generated.actions

import org.hildan.minecraft.mining.optimizer.geometry.Range3D

/**
 * Represents a 3D range of what can be dug, which is symmetric with respect to the XY, YZ, and XZ planes, and has
 * symmetric behaviours for X and Z (symmetric with respect to the 45°-diagonal vertical planes).
 */
enum class DigRange3D(
    /**
     * `boundsDistribution[y][x]` gives the maximum that Z can take for the given X and Y.
     */
    private val boundsDistribution: Array<IntArray>
) : Range3D {

    /**
     * The accessible range when pressing shift and no block is preventing the player from overextending.
     */
    PRESSING_SHIFT(
        arrayOf<IntArray>( //
            intArrayOf(6, 6, 6, 5, 5, 4, 2), // distanceY = 0
            intArrayOf(6, 6, 6, 5, 5, 4, 2), // distanceY = 1 or -1
            intArrayOf(6, 6, 5, 5, 5, 4, 1), // distanceY = 2 or -2
            intArrayOf(5, 5, 5, 5, 4, 3, -1) // distanceY = 3 or -3
        )
    ),
    /**
     * The accessible range when the player is strictly limited to the block he's standing on (because other blocks are
     * in his way and prevent him from overextending by pressing shift).
     */
    STRICT(
        arrayOf<IntArray>( //
            intArrayOf(5, 5, 5, 5, 4, 3), // distanceY = 0
            intArrayOf(5, 5, 5, 5, 4, 3), // distanceY = 1 or -1
            intArrayOf(5, 5, 5, 4, 3, 2), // distanceY = 2 or -2
            intArrayOf(4, 4, 4, 4, 3, -1) // distanceY = 3 or -3
        )
    );

    override fun maxX(distanceY: Int): Int = boundsDistribution[Math.abs(distanceY)].size - 1

    override fun maxY(): Int = boundsDistribution.size - 1

    override fun inRange(distanceX: Int, distanceY: Int, distanceZ: Int): Boolean = when {
        distanceY < minY() || distanceY > maxY() -> false
        distanceX < minX(distanceY) || distanceX > maxX(distanceY) -> false
        else -> Math.abs(distanceZ) <= boundsDistribution[Math.abs(distanceY)][Math.abs(distanceX)]
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (dY in 0..maxY()) {
            for (dX in minX(dY)..maxX(dY)) {
                for (dZ in minZ(dY)..maxZ(dY)) {
                    if (inRange(dX, dY, dZ)) {
                        sb.append('.')
                    } else {
                        sb.append('X')
                    }
                }
                sb.append(String.format("%n"))
            }
            sb.append(String.format("%n"))
        }
        return sb.toString()
    }
}
