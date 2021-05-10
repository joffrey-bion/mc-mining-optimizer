package org.hildan.minecraft.mining.optimizer.geometry

import kotlin.math.abs

/**
 * Represents a 3D range of what can be dug, which is symmetric with respect to the XY, YZ, and XZ planes, and has
 * symmetric behaviours for X and Z (symmetric with respect to the 45°-diagonal vertical planes).
 */
enum class DigRange3D(
    /**
     * `boundsDistribution[y][x]` gives the maximum that Z can take for the given X and Y.
     */
    private val boundsDistribution: Array<IntArray>,
) : Range3D {

    /**
     * The accessible range when pressing shift and no block is preventing the player from overextending.
     */
    PRESSING_SHIFT(arrayOf(
        intArrayOf(6, 6, 6, 5, 5, 4, 2), // distanceY = 0
        intArrayOf(6, 6, 6, 5, 5, 4, 2), // distanceY = 1 or -1
        intArrayOf(6, 6, 5, 5, 5, 4, 1), // distanceY = 2 or -2
        intArrayOf(5, 5, 5, 5, 4, 3, -1), // distanceY = 3 or -3
    )),

    /**
     * The accessible range when the player is strictly limited to the block he's standing on (because other blocks are
     * in his way and prevent him from overextending by pressing shift).
     */
    STRICT(arrayOf(
        intArrayOf(5, 5, 5, 5, 4, 3), // distanceY = 0
        intArrayOf(5, 5, 5, 5, 4, 3), // distanceY = 1 or -1
        intArrayOf(5, 5, 5, 4, 3, 2), // distanceY = 2 or -2
        intArrayOf(4, 4, 4, 4, 3, -1), // distanceY = 3 or -3
    ));

    override fun maxX(distanceY: Int): Int = boundsDistribution[abs(distanceY)].lastIndex

    override fun maxY(): Int = boundsDistribution.lastIndex

    override fun inRange(distanceX: Int, distanceY: Int, distanceZ: Int): Boolean = when {
        distanceY < minY() || distanceY > maxY() -> false
        distanceX < minX(distanceY) || distanceX > maxX(distanceY) -> false
        else -> abs(distanceZ) <= boundsDistribution[abs(distanceY)][abs(distanceX)]
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (dY in 0..maxY()) {
            for (dX in minX(dY)..maxX(dY)) {
                for (dZ in minZ(dY)..maxZ(dY)) {
                    val c = if (inRange(dX, dY, dZ)) '.' else 'X'
                    sb.append(c)
                }
                sb.appendLine()
            }
            sb.appendLine()
        }
        return sb.toString()
    }
}
