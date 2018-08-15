package org.hildan.minecraft.mining.optimizer.geometry

/**
 * Represents a 3D range.
 *
 * The min and max methods give the interval of variation of each variable. These intervals of variation are just bounds
 * for enumeration. They are not sufficient to tell if a point in space is in this range. For that purpose, use the
 * [inRange] method.
 */
interface Range3D {

    /**
     * Returns the maximum value Y can take in this range.
     *
     * @return the maximum value Y can take in this range.
     */
    fun maxY(): Int

    /**
     * Returns the minimum value Y can take in this range.
     *
     * By default, the interval is symmetric and this is equivalent to [`-maxY()`][maxY].
     *
     * @return the minimum value Y can take in this range.
     */
    fun minY(): Int = -maxY()

    /**
     * Returns the maximum value X can take when Z varies, at the given Y distance of the center.
     *
     * @param distanceY the distance from the center on the Y axis
     * @return the maximum value X can take when Z varies, at the given Y distance of the center.
     */
    fun maxX(distanceY: Int): Int

    /**
     * Returns the minimum value X can take when Z varies, at the given Y distance of the center.
     *
     * By default, the interval is symmetric and this is equivalent to [`-maxX()`][maxX].
     *
     * @param distanceY the distance from the center on the Y axis
     * @return the minimum value X can take when Z varies, at the given Y distance of the center.
     */
    fun minX(distanceY: Int): Int = -maxX(distanceY)

    /**
     * Returns the maximum value Z can take when X varies, at the given Y distance of the center.
     *
     * By default the range behaves symmetrically for X and Z, and this is equivalent to [maxX].
     *
     * @param distanceY the distance from the center on the Y axis
     * @return the maximum value Z can take when X varies, at the given Y distance of the center.
     */
    fun maxZ(distanceY: Int): Int = maxX(distanceY)

    /**
     * Returns the minimum value Z can take when X varies, at the given Y distance of the center.
     *
     * By default, the interval is symmetric and this is equivalent to [`-maxZ(distanceY)`][maxZ].
     *
     * @param distanceY the distance from the center on the Y axis
     * @return the minimum value Z can take when X varies, at the given Y distance of the center.
     */
    fun minZ(distanceY: Int): Int = -maxZ(distanceY)

    /**
     * Returns whether the given point is in range.
     *
     * @param distanceX the X distance from the center
     * @param distanceY the Y distance from the center
     * @param distanceZ the Z distance from the center
     * @return true if the given point is in range
     */
    fun inRange(distanceX: Int, distanceY: Int, distanceZ: Int): Boolean
}
