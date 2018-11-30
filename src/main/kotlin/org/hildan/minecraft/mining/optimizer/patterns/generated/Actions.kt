package org.hildan.minecraft.mining.optimizer.patterns.generated

import org.hildan.minecraft.mining.optimizer.blocks.Sample
import org.hildan.minecraft.mining.optimizer.geometry.DigRange3D
import org.hildan.minecraft.mining.optimizer.geometry.Dimensions
import org.hildan.minecraft.mining.optimizer.geometry.Distance3D
import org.hildan.minecraft.mining.optimizer.geometry.Position
import org.hildan.minecraft.mining.optimizer.geometry.Range3D
import org.hildan.minecraft.mining.optimizer.geometry.Wrapping
import java.util.ArrayList

/**
 * Gets all possible actions supported for the given [digRange].
 */
fun allActionsFor(digRange: DigRange3D) = MoveAction.ALL + RelativeDigAction.generateAllFor(digRange)

/**
 * An action the player can perform. An action is performed on a sample, from a given position.
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
data class MoveAction(private val distance: Distance3D) : Action() {

    init {
        if (distance.y > 1) {
            throw IllegalArgumentException("Can't jump higher than 1 block")
        }
        if (distance.y < -1) {
            throw IllegalArgumentException("No going down lower than 1 block, to be able to go back")
        }
        if (distance.x == 0 && distance.z == 0) {
            throw IllegalArgumentException("Cannot stay in the same horizontal place, falls are not actions")
        }
        if (distance.x != 0 && distance.z != 0) {
            throw IllegalArgumentException("Moves are accepted only along one axis at a time")
        }
        if (Math.abs(distance.x) > 1 || Math.abs(distance.z) > 1) {
            throw IllegalArgumentException("Only moves of one block are accepted")
        }
    }

    override fun isValidFor(sample: Sample, currentHeadPosition: Position): Boolean {
        // check that there is room for the head
        val headDestination = sample.getBlock(currentHeadPosition, distance, Wrapping.WRAP_XZ)
        if (headDestination == null || !headDestination.isDug) {
            return false
        }
        // check that there is room for the feet
        val feetDestination = sample.getBlockBelow(headDestination, Wrapping.WRAP_XZ)
        if (feetDestination == null || !feetDestination.isDug) {
            return false
        }
        // check that there is room for the movement
        return hasRoomForMovement(sample, currentHeadPosition, headDestination)
    }

    private fun hasRoomForMovement(sample: Sample, headPositionBefore: Position, headPositionAfter: Position) =
        when (distance.y) {
            0 -> true
            1 -> canJumpBeforeMoving(sample, headPositionBefore)
            -1 -> canMoveBeforeFalling(sample, headPositionAfter)
            else ->
                // can't jump higher than 1
                // and can't fall too low because we want to be able to go back
                false
        }

    private fun canJumpBeforeMoving(sample: Sample, headPositionBefore: Position) =
        isAboveBlockDug(sample, headPositionBefore)

    private fun canMoveBeforeFalling(sample: Sample, headPositionAfter: Position) =
        isAboveBlockDug(sample, headPositionAfter)

    private fun isAboveBlockDug(sample: Sample, headPositionBefore: Position) =
        sample.getBlockAbove(headPositionBefore, Wrapping.CUT)!!.isDug

    fun move(currentHeadPosition: Position, dimensions: Dimensions): Position =
        dimensions.getPos(currentHeadPosition, distance, Wrapping.WRAP)!!

    override fun toString(): String = "MoveOf($distance)"

    companion object {

        val ALL: Collection<Action> by lazy {
            val values = intArrayOf(0, 1, -1)
            ArrayList<MoveAction>(12).apply {
                for (dy in values) {
                    for (dx in values) {
                        for (dz in values) {
                            // we have to move horizontally
                            if (dx == 0 && dz == 0) continue

                            // we don't want diagonal moves
                            if (dx != 0 && dz != 0) continue

                            add(MoveAction(Distance3D.of(dx, dy, dz)))
                        }
                    }
                }
            }
        }
    }
}

/**
 * An immutable action representing the player digging one block in an acceptable range. Digging above or below the
 * player is forbidden (we don't want to fall in a cave, or be covered in lava).
 */
data class RelativeDigAction(private val distanceFromHead: Distance3D) : Action() {

    override fun isValidFor(sample: Sample, currentHeadPosition: Position): Boolean {
        // we wrap horizontally because we want to allow diagonally shaped patterns to exist
        // we don't wrap vertically because the probabilities of ores are not equivalent at the top and bottom
        val blockToDig = sample.getBlock(currentHeadPosition, distanceFromHead, Wrapping.WRAP_XZ)
        return blockToDig != null && !blockToDig.isDug && isPathClear(sample, currentHeadPosition, blockToDig)
    }

    private fun isPathClear(sample: Sample, head: Position, block: Position): Boolean {
        when (distanceFromHead.sqNorm) {
            1 -> return true // block next to head always accessible
            2 -> when (distanceFromHead.y) {
                -1 -> return true // block next to feet always accessible
                1 -> return canDigAroundBlockAboveHead(sample, head, block)
            }
        }
        // TODO implement true algorithm to check that the view is not obstructed
        return false
    }

    private fun canDigAroundBlockAboveHead(sample: Sample, head: Position, block: Position): Boolean {
        // we know the target block is within Y bounds, the block above the head is at the same level, so it fits too
        val aboveHead = sample.getBlockAbove(head, Wrapping.CUT)!!
        // we know the head is within Y bounds, the block below target is around the head, so it fits too
        val belowTarget = sample.getBlockBelow(block, Wrapping.CUT)!!
        return aboveHead.isDug || belowTarget.isDug
    }

    fun digPosition(currentHeadPosition: Position, dimensions: Dimensions): Position =
        dimensions.getPos(currentHeadPosition, distanceFromHead, Wrapping.WRAP_XZ)!!

    override fun toString(): String = "Dig($distanceFromHead)"

    companion object {

        /**
         * Gets all the possible digging actions for the given accepted range.
         *
         * @param range the digging range of the player
         * @return a collection of actions that can potentially be done
         */
        fun generateAllFor(range: Range3D): Collection<Action> {
            val moves = mutableListOf<RelativeDigAction>()
            for (dY in range.minY()..range.maxY()) {
                for (dX in range.minX(dY)..range.maxX(dY)) {
                    for (dZ in range.minZ(dY)..range.maxZ(dY)) {
                        if (dX == 0 && dZ == 0) {
                            continue // never dig above the head or below the feet
                        }
                        if (range.inRange(dX, dY, dZ)) {
                            moves.add(RelativeDigAction(Distance3D.of(dX, dY, dZ)))
                        }
                    }
                }
            }
            return moves.sortedBy { it.distanceFromHead.sqNorm }
        }
    }
}
