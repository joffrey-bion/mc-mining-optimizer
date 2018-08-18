package org.hildan.minecraft.mining.optimizer.patterns.generated.actions

import org.hildan.minecraft.mining.optimizer.blocks.Sample
import org.hildan.minecraft.mining.optimizer.blocks.Wrapping
import org.hildan.minecraft.mining.optimizer.geometry.Position
import java.util.ArrayList

/**
 * An immutable action representing the player moving of one block horizontally. The move can be done in any 4
 * horizontal directions (no diagonal), and can result in the player going up or down one block as well.
 */
data class MoveAction(
    private val distanceX: Int,
    private val distanceY: Int,
    private val distanceZ: Int
) : Action {

    init {
        if (distanceY > 1) {
            throw IllegalArgumentException("Can't jump higher than 1 block")
        }
        if (distanceY < -1) {
            throw IllegalArgumentException("No going down lower than 1 block, to be able to go back")
        }
        if (distanceX == 0 && distanceZ == 0) {
            throw IllegalArgumentException("Cannot stay in the same horizontal place, falls are not actions")
        }
        if (distanceX != 0 && distanceZ != 0) {
            throw IllegalArgumentException("Moves are accepted only along one axis at a time")
        }
        if (Math.abs(distanceX) > 1 || Math.abs(distanceZ) > 1) {
            throw IllegalArgumentException("Only moves of one block are accepted")
        }
    }

    override fun isValidFor(sample: Sample, currentHeadPosition: Position): Boolean {
        // check that there is room for the head
        val headDestination = sample.getBlock(currentHeadPosition, distanceX, distanceY, distanceZ, Wrapping.CUT)
        if (headDestination == null || !headDestination.isDug) {
            return false
        }
        // check that there is room for the feet
        val feetDestination = sample.getBlockBelow(headDestination, Wrapping.CUT)
        if (feetDestination == null || !feetDestination.isDug) {
            return false
        }
        // check that there is room for the movement
        return hasRoomForMovement(sample, currentHeadPosition, headDestination)
    }

    /**
     * Checks whether the intermediate position of the player during the movement is clear.
     *
     * @param sample
     * the current sample
     * @param headPositionBefore
     * the position of the head before the movement
     * @param headPositionAfter
     * the position of the head after the movement
     * @return true if the intermediate block is clear
     */
    private fun hasRoomForMovement(sample: Sample, headPositionBefore: Position, headPositionAfter: Position) =
        when (distanceY) {
            0 -> true
            1 -> {
                val jumpRoom = sample.getBlockAbove(headPositionBefore, Wrapping.CUT)
                jumpRoom != null && jumpRoom.isDug
            }
            -1 -> {
                val forwardRoom = sample.getBlockAbove(headPositionAfter, Wrapping.CUT)
                forwardRoom != null && forwardRoom.isDug
            }
            else ->
                // can't jump higher than 1
                // (counts also for the negative Ys because we want to be able to go back)
                false
        }

    override fun executeOn(sample: Sample, currentHeadPosition: Position): Position =
        sample.getBlock(currentHeadPosition, distanceX, distanceY, distanceZ, Wrapping.CUT)!!

    override fun isInverseOf(action: Action): Boolean = when (action) {
        !is MoveAction -> false
        else -> action.distanceX == -distanceX && action.distanceY == -distanceY && action.distanceZ == -distanceZ
    }

    override fun toString(): String = "MoveOf($distanceX,$distanceY,$distanceZ)"

    companion object {

        private val values = intArrayOf(0, 1, -1)

        val all: Collection<Action>
            get() {
                val moves = ArrayList<MoveAction>(12)
                for (y in values) {
                    for (x in values) {
                        for (z in values) {
                            // we have to move horizontally
                            if (x == 0 && z == 0) continue

                            // we don't want diagonal moves
                            if (x != 0 && z != 0) continue

                            moves.add(MoveAction(x, y, z))
                        }
                    }
                }
                return moves
            }
    }
}
