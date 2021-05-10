package org.hildan.minecraft.mining.optimizer.patterns.generated

import org.hildan.minecraft.mining.optimizer.geometry.BlockIndex
import org.hildan.minecraft.mining.optimizer.geometry.DigRange3D
import org.hildan.minecraft.mining.optimizer.geometry.Dimensions
import org.hildan.minecraft.mining.optimizer.geometry.Distance3D
import org.hildan.minecraft.mining.optimizer.geometry.Range3D
import java.util.ArrayList
import kotlin.math.abs

/**
 * Gets all possible actions supported for the given [digRange].
 */
fun allActionsFor(digRange: DigRange3D) = MoveAction.ALL + RelativeDigAction.allIn(digRange)

/**
 * An action the player can perform. An action is performed on a sample, from a given position.
 */
sealed class Action {
    /**
     * Checks whether it is possible to execute this action from the given [currentHeadPosition] in the given [sample].
     */
    abstract fun isValidFor(sample: DigMatrix, currentHeadPosition: BlockIndex): Boolean
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
        if (abs(distance.x) > 1 || abs(distance.z) > 1) {
            throw IllegalArgumentException("Only moves of one block are accepted")
        }
    }

    override fun isValidFor(sample: DigMatrix, currentHeadPosition: BlockIndex): Boolean = with (sample.dimensions) {
        val headDestination = (currentHeadPosition + distance) ?: return false
        val feetDestination = headDestination.below ?: return false

        if (!canStandAtPosition(sample, headDestination, feetDestination)) {
            return false
        }
        return hasRoomForMovement(sample, currentHeadPosition, headDestination)
    }

    private fun canStandAtPosition(sample: DigMatrix, head: BlockIndex, feet: BlockIndex) =
        sample.isDug(head) && sample.isDug(feet) && sample.isBlockOrBottomBelow(feet)

    private fun hasRoomForMovement(sample: DigMatrix, headPositionBefore: BlockIndex, headPositionAfter: BlockIndex) =
        when (distance.y) {
            0 -> true // we only move by one, so there can't be a block on the way
            1 -> canJumpBeforeMoving(sample, headPositionBefore)
            -1 -> canMoveBeforeFalling(sample, headPositionAfter)
            else ->
                // can't jump higher than 1
                // and can't fall too low because we want to be able to go back
                false
        }

    private fun canJumpBeforeMoving(sample: DigMatrix, headPositionBefore: BlockIndex) =
        sample.isDugAbove(headPositionBefore)

    private fun canMoveBeforeFalling(sample: DigMatrix, headPositionAfter: BlockIndex) =
        sample.isDugAbove(headPositionAfter)

    fun move(currentHeadPosition: BlockIndex, dimensions: Dimensions): BlockIndex = with(dimensions) {
        (currentHeadPosition + distance)!!
    }

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

    override fun isValidFor(sample: DigMatrix, currentHeadPosition: BlockIndex): Boolean = with(sample.dimensions) {
        val blockToDig = (currentHeadPosition + distanceFromHead) ?: return false
        return !sample.isDug(blockToDig) && isPathClear(sample, currentHeadPosition, blockToDig)
    }

    private fun isPathClear(sample: DigMatrix, head: BlockIndex, block: BlockIndex): Boolean {
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

    private fun canDigAroundBlockAboveHead(sample: DigMatrix, head: BlockIndex, target: BlockIndex): Boolean = with(sample.dimensions) {
        // we know the target block is within Y bounds, the block above the head is at the same level, so it fits too
        // we know the head is within Y bounds, the block below target is around the head, so it fits too
        sample.isDug(head.above!!) || sample.isDug(target.below!!)
    }

    fun digPosition(currentHeadPosition: BlockIndex, dimensions: Dimensions): BlockIndex = with(dimensions) {
        (currentHeadPosition + distanceFromHead)!!
    }

    override fun toString(): String = "Dig($distanceFromHead)"

    companion object {
        /**
         * Gets all the possible digging actions for the given accepted [range].
         */
        fun allIn(range: Range3D): Collection<Action> = range.allDistancesInRange()
            .filterNot { it.x == 0 && it.z == 0 } // never dig above the head or below the feet
            .sortedBy { it.sqNorm }
            .map(::RelativeDigAction)
            .toList()
    }
}
