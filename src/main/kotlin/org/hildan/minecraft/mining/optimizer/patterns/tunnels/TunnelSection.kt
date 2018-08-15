package org.hildan.minecraft.mining.optimizer.patterns.tunnels

import org.hildan.minecraft.mining.optimizer.blocks.Sample
import org.hildan.minecraft.mining.optimizer.geometry.Axis

/**
 * Describes the dimensions of a 2D section of a tunnel.
 */
data class TunnelSection(val width: Int, val height: Int) {

    fun digInto(sample: Sample, originX: Int, originY: Int, originZ: Int, length: Int, direction: Axis) {
        val xMax = originX + getSizeOnAxis(Axis.X, direction, length)
        val yMax = originY + getSizeOnAxis(Axis.Y, direction, length)
        val zMax = originZ + getSizeOnAxis(Axis.Z, direction, length)
        for (x in originX until Math.min(xMax, sample.width)) {
            for (y in originY until Math.min(yMax, sample.height)) {
                for (z in originZ until Math.min(zMax, sample.length)) {
                    sample.digBlock(x, y, z)
                }
            }
        }
    }

    private fun getSizeOnAxis(axis: Axis, tunnelDirection: Axis, length: Int): Int = when (axis) {
        tunnelDirection -> length
        Axis.Y -> height
        else -> width
    }

    companion object {
        val MAN_SIZED = TunnelSection(1, 2)
        val DOUBLE_MAN_SIZED = TunnelSection(2, 2)
        val BIG_CORRIDOR = TunnelSection(2, 3)
    }
}
