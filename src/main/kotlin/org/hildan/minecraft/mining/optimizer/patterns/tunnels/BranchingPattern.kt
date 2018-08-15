package org.hildan.minecraft.mining.optimizer.patterns.tunnels

import org.hildan.minecraft.mining.optimizer.blocks.Sample
import org.hildan.minecraft.mining.optimizer.geometry.Axis
import org.hildan.minecraft.mining.optimizer.patterns.AbstractDiggingPattern
import org.hildan.minecraft.mining.optimizer.patterns.Access

/**
 * One main shaft with perpendicular branches.
 */
class BranchingPattern(
    private val shaft: TunnelPattern,
    private val branch: TunnelPattern,
    private val branchLength: Int,
    private val branchOffsetByTier: Int
) : AbstractDiggingPattern() {

    private val layerHeight = shaft.section.height + shaft.vSpacing

    // the offset doesn't matter here, the spatial period is the same
    override val width = 2 * branchLength + shaft.section.width

    // with an offset, two consecutive layers are different
    override val height = layerHeight * 2

    override val length = branch.hSpacing + branch.section.width

    init {
        if (shaft.section.height < branch.section.height) {
            throw IllegalArgumentException("The main shaft should be higher than branches")
        }
        if (shaft.hSpacing < 2 * branch.section.width) {
            throw IllegalArgumentException(
                "Branches from 2 different shafts are touching: reduce branch length, or put more space"
            )
        }
    }

    override fun getAccesses(originX: Int, originY: Int) = setOf(
        Access(originX + branchLength, originY),
        Access(originX + branchLength, originY + layerHeight)
    )

    override fun digInto(sample: Sample, originX: Int, originY: Int, originZ: Int) {
        digLayer(sample, originX, originY, originZ, 0)
        digLayer(sample, originX, originY + layerHeight, originZ, branchOffsetByTier)
    }

    private fun digLayer(sample: Sample, originX: Int, originY: Int, originZ: Int, offset: Int) {
        branch.digInto(sample, originX, originY, originZ + offset, branchLength, Axis.X)
        shaft.digInto(sample, originX + branchLength, originY, originZ, length, Axis.Z)
        val oppositeBranchStartX = originX + branchLength + shaft.section.width
        branch.digInto(sample, oppositeBranchStartX, originY, originZ + offset, branchLength, Axis.X)
    }
}
