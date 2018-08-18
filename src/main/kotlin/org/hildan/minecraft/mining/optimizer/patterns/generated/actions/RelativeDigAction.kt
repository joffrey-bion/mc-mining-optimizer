package org.hildan.minecraft.mining.optimizer.patterns.generated.actions

import org.hildan.minecraft.mining.optimizer.blocks.Sample
import org.hildan.minecraft.mining.optimizer.blocks.Wrapping
import org.hildan.minecraft.mining.optimizer.geometry.Position
import org.hildan.minecraft.mining.optimizer.geometry.Range3D

/**
 * An immutable action representing the player digging one block in an acceptable range. Digging above or below the
 * player is forbidden (we don't want to fall in a cave, or be covered in lava).
 */
data class RelativeDigAction(
    private val distanceX: Int,
    private val distanceY: Int,
    private val distanceZ: Int
) : Action {

    init {
        if (distanceX == 0 && distanceZ == 0) {
            throw IllegalArgumentException("Never dig above the head or below the feet")
        }
    }

    /**
     * Returns the squared distance of the block to dig.
     *
     * @return the squared distance of the block to dig.
     */
    private fun norm(): Int = distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ

    override fun isValidFor(sample: Sample, currentHeadPosition: Position): Boolean {
        val blockToDig = sample.getBlock(currentHeadPosition, distanceX, distanceY, distanceZ, Wrapping.CUT)
        return blockToDig != null && !blockToDig.isDug && isPathClear(sample, currentHeadPosition, blockToDig)
    }

    private fun isPathClear(sample: Sample, head: Position, block: Position): Boolean {
        val norm = norm()
        if (norm == 1) {
            // block next to head always accessible
            return true
        }
        if (norm == 2) {
            if (distanceY == -1) {
                // block next to feet always accessible
                return true
            }
            if (distanceY == 1) {
                val aboveHead = sample.getBlockAbove(head, Wrapping.WRAP)
                val belowTarget = sample.getBlockBelow(block, Wrapping.WRAP)
                return aboveHead!!.isDug || belowTarget!!.isDug
            }
        }

        // TODO implement true algorithm to check that the view is not obstructed

        return false
    }

    override fun executeOn(sample: Sample, currentHeadPosition: Position): Position {
        val blockToDig = sample.getBlock(currentHeadPosition, distanceX, distanceY, distanceZ, Wrapping.CUT)!!
        sample.digBlock(blockToDig)
        // we haven't moved
        return currentHeadPosition
    }

    // can't inverse a dig action
    override fun isInverseOf(action: Action): Boolean = false

    override fun toString(): String = "Dig($distanceX,$distanceY,$distanceZ)"

    companion object {

        /**
         * Gets all the possible digging actions for the given accepted range.
         *
         * @param range the digging range of the player
         * @return a collection of actions that can potentially be done
         */
        fun getAll(range: Range3D): Collection<Action> {
            val moves = mutableListOf<RelativeDigAction>()
            for (dY in range.minY()..range.maxY()) {
                for (dX in range.minX(dY)..range.maxX(dY)) {
                    for (dZ in range.minZ(dY)..range.maxZ(dY)) {
                        if (dX == 0 && dZ == 0) {
                            continue // never dig above the head or below the feet
                        }
                        if (range.inRange(dX, dY, dZ)) {
                            moves.add(RelativeDigAction(dX, dY, dZ))
                        }
                    }
                }
            }
            return moves.sortedBy { it.norm() }
        }
    }
}
