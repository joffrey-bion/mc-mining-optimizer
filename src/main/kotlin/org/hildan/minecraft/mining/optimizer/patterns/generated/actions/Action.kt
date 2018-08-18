package org.hildan.minecraft.mining.optimizer.patterns.generated.actions

import org.hildan.minecraft.mining.optimizer.blocks.Sample
import org.hildan.minecraft.mining.optimizer.blocks.Wrapping
import org.hildan.minecraft.mining.optimizer.geometry.Dimensions
import org.hildan.minecraft.mining.optimizer.geometry.Position
import org.hildan.minecraft.mining.optimizer.geometry.Range3D
import java.util.ArrayList

/**
 * Represents an action the player can perform. An action is performed on a sample, from a given position.
 */
sealed class Action {

    /**
     * Checks whether it is possible to execute this action in the given situation.
     *
     * @param sample the current sample
     * @param currentHeadPosition the current position of the head of the player
     * @return true if this action can be performed in the given situation
     */
    abstract fun isValidFor(sample: Sample, currentHeadPosition: Position): Boolean
}

/**
 * An immutable action representing the player moving of one block horizontally. The move can be done in any 4
 * horizontal directions (no diagonal), and can result in the player going up or down one block as well.
 */
data class MoveAction(
    private val distanceX: Int,
    private val distanceY: Int,
    private val distanceZ: Int
) : Action() {

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
        // TODO check block wrapping here

        // check that there is room for the head
        val headDestination = sample.getBlock(currentHeadPosition, distanceX, distanceY, distanceZ, Wrapping.WRAP)!!
        if (!headDestination.isDug) {
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

    private fun hasRoomForMovement(sample: Sample, headPositionBefore: Position, headPositionAfter: Position) =
        when (distanceY) {
            0 -> true
            1 -> canJumpBeforeMoving(sample, headPositionBefore)
            -1 -> canMoveBeforeFalling(sample, headPositionAfter)
            else ->
                // can't jump higher than 1
                // (counts also for the negative Ys because we want to be able to go back)
                false
        }

    private fun canJumpBeforeMoving(sample: Sample, headPositionBefore: Position) =
        isAboveBlockDug(sample, headPositionBefore)

    private fun canMoveBeforeFalling(sample: Sample, headPositionAfter: Position) =
        isAboveBlockDug(sample, headPositionAfter)

    private fun isAboveBlockDug(sample: Sample, headPositionBefore: Position) =
        sample.getBlockAbove(headPositionBefore, Wrapping.CUT)!!.isDug

    fun move(currentHeadPosition: Position, dimensions: Dimensions): Position =
        dimensions.getPos(currentHeadPosition, distanceX, distanceY, distanceZ, Wrapping.WRAP)!!

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

/**
 * An immutable action representing the player digging one block in an acceptable range. Digging above or below the
 * player is forbidden (we don't want to fall in a cave, or be covered in lava).
 */
data class RelativeDigAction(
    private val distanceX: Int,
    private val distanceY: Int,
    private val distanceZ: Int
) : Action() {
    /**
     * Returns the squared distance of the block to dig.
     *
     * @return the squared distance of the block to dig.
     */
    private fun norm(): Int = distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ

    override fun isValidFor(sample: Sample, currentHeadPosition: Position): Boolean {
        val blockToDig = sample.getBlock(
            currentHeadPosition, distanceX, distanceY, distanceZ,
            Wrapping.CUT
        )
        return blockToDig != null && !blockToDig.isDug && isPathClear(sample, currentHeadPosition, blockToDig)
    }

    private fun isPathClear(sample: Sample, head: Position, block: Position): Boolean {
        val norm = norm()
        when (norm) {
            1 -> return true // block next to head always accessible
            2 -> when (distanceY) {
                -1 -> return true // block next to feet always accessible
                1 -> {
                    val aboveHead = sample.getBlockAbove(head, Wrapping.WRAP)!!
                    val belowTarget = sample.getBlockBelow(block, Wrapping.WRAP)!!
                    return aboveHead.isDug || belowTarget.isDug
                }
            }
        }
        // TODO implement true algorithm to check that the view is not obstructed
        return false
    }

    fun digPosition(currentHeadPosition: Position, dimensions: Dimensions): Position =
        dimensions.getPos(currentHeadPosition, distanceX, distanceY, distanceZ, Wrapping.WRAP)!!

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
