package org.hildan.minecraft.mining.optimizer.patterns.tunnels

import org.hildan.minecraft.mining.optimizer.blocks.Sample

/**
 * Defines how to dig a tunnel. It is based on a [TunnelSection] repeated horizontally and vertically.
 */
data class TunnelPattern(
    val section: TunnelSection,
    val hSpacing: Int,
    val vSpacing: Int
) {
    fun digInto(sample: Sample, originX: Int, originY: Int, originZ: Int, length: Int, direction: Axis) =
        section.digInto(sample, originX, originY, originZ, length, direction)

    companion object {
        val STANDARD_SHAFT = TunnelPattern(TunnelSection.DOUBLE_MAN_SIZED, 23, 2)
        val BIG_SHAFT = TunnelPattern(TunnelSection.BIG_CORRIDOR, 23, 1)
        val STANDARD_BRANCH_2SPACED = TunnelPattern(TunnelSection.MAN_SIZED, 2, -1)
        val STANDARD_BRANCH_3SPACED = TunnelPattern(TunnelSection.MAN_SIZED, 3, -1)
    }
}
