package org.hildan.minecraft.mining.optimizer.geometry;

/**
 * Represents a 3D range.
 * <p>
 * The min and max methods give the interval of variation of each variable. These intervals of variation are just bounds
 * for enumeration. They are not sufficient to tell if a point in space is in this range. For that purpose, use the
 * {@link #inRange(int, int, int)} method.
 */
public interface Range3D {

    /**
     * Returns the maximum value Y can take in this range.
     *
     * @return the maximum value Y can take in this range.
     */
    int maxY();

    /**
     * Returns the minimum value Y can take in this range.
     * <p>
     * By default, the interval is symmetric and this is equivalent to {@code -maxY()}.
     *
     * @return the minimum value Y can take in this range.
     */
    default int minY() {
        return -maxY();
    }

    /**
     * Returns the maximum value X can take when Z varies, at the given Y distance of the center.
     *
     * @param distanceY
     *         the distance from the center on the Y axis
     * @return the maximum value X can take when Z varies, at the given Y distance of the center.
     */
    int maxX(int distanceY);

    /**
     * Returns the minimum value X can take when Z varies, at the given Y distance of the center.
     * <p>
     * By default, the interval is symmetric and this is equivalent to {@code -maxX()}.
     *
     * @param distanceY
     *         the distance from the center on the Y axis
     * @return the minimum value X can take when Z varies, at the given Y distance of the center.
     */
    default int minX(int distanceY) {
        return -maxX(distanceY);
    }

    /**
     * Returns the maximum value Z can take when X varies, at the given Y distance of the center.
     * <p>
     * By default the range behaves symmetrically for X and Z, and this is equivalent to {@link #maxX(int)}}.
     *
     * @param distanceY
     *         the distance from the center on the Y axis
     * @return the maximum value Z can take when X varies, at the given Y distance of the center.
     */
    default int maxZ(int distanceY) {
        return maxX(distanceY);
    }

    /**
     * Returns the minimum value Z can take when X varies, at the given Y distance of the center.
     * <p>
     * By default, the interval is symmetric and this is equivalent to {@code -maxZ(distanceY)}.
     *
     * @param distanceY
     *         the distance from the center on the Y axis
     * @return the minimum value Z can take when X varies, at the given Y distance of the center.
     */
    default int minZ(int distanceY) {
        return -maxZ(distanceY);
    }

    /**
     * Returns whether the given point is in range.
     *
     * @param distanceX
     *         the X distance from the center
     * @param distanceY
     *         the Y distance from the center
     * @param distanceZ
     *         the Z distance from the center
     * @return true if the given point is in range
     */
    boolean inRange(int distanceX, int distanceY, int distanceZ);
}
