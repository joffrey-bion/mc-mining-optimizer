package org.hildan.minecraft.mining.optimizer.patterns.generated

import org.hildan.minecraft.mining.optimizer.blocks.Sample
import org.hildan.minecraft.mining.optimizer.geometry.DigRange3D
import org.hildan.minecraft.mining.optimizer.geometry.Dimensions
import org.hildan.minecraft.mining.optimizer.geometry.Position
import org.hildan.minecraft.mining.optimizer.patterns.Access
import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern
import org.hildan.minecraft.mining.optimizer.patterns.generated.actions.Action
import org.hildan.minecraft.mining.optimizer.patterns.generated.actions.MoveAction
import org.hildan.minecraft.mining.optimizer.patterns.generated.actions.RelativeDigAction
import java.util.ArrayList
import java.util.HashMap

/**
 * Represents a state while digging a sample. It contains the history that brought to this state and can be
 * [replayed on][replayOn] a [Sample]. It can generate the next possible states from this point using [expand].
 *
 * Can be turned into a [DiggingPattern].
 */
internal data class DiggingState(
    /**
     * When multiple accesses are available, we track the head position for each access independently. Hence one head
     * position per access.
     */
    private val headPositionByAccess: Map<Access, Position>,

    private val dugPositions: List<Position>
) {
    fun replayOn(sample: Sample) {
        dugPositions.forEach { sample.digBlock(it) }
    }

    /**
     * Expands the current state by performing every possible action on it. This method does not affect this state.
     *
     * @param sample a test sample reflecting this state, which can be used to test the next possible actions. Such a
     * sample can be created using [replayOn].
     * @param constraints some general constraints to limit to possible actions
     *
     * @return the collection of all states resulting of the execution of each possible action on this state.
     */
    fun expand(sample: Sample, constraints: GenerationConstraints): Collection<DiggingState> {
        if (dugPositions.size >= constraints.maxDugBlocks) {
            return emptyList()
        }
        return headPositionByAccess.flatMap { this.expandAccess(it.key, sample) }
    }

    /**
     * Streams the states representing all possible ways of continuing in the given access.
     *
     * @param access the access to operate on
     * @return a Stream of states resulting of each possible action taken on the given access
     */
    private fun expandAccess(access: Access, sample: Sample) = allActions
        .filter { action -> action.isValidFor(sample, headPositionByAccess[access]!!) }
        .map { action -> next(sample, access, action) }

    /**
     * Returns the `DiggingState` resulting of the execution of the given action on this state. This method does
     * not affect this state.
     *
     * @param access the access for which to add the action
     * @param action the action to perform on this state
     * @return the resulting state
     */
    private fun next(sample: Sample, access: Access, action: Action): DiggingState = when (action) {
        is RelativeDigAction -> digFromHere(access, action, sample.dimensions)
        is MoveAction -> moveFromHere(access, action, sample.dimensions)
    }

    private fun moveFromHere(access: Access, action: MoveAction, dimensions: Dimensions): DiggingState {
        val newHeadPositionsPerAccess = HashMap(headPositionByAccess)
        newHeadPositionsPerAccess[access] = action.move(headPositionByAccess[access]!!, dimensions)
        return DiggingState(newHeadPositionsPerAccess, dugPositions)
    }

    private fun digFromHere(access: Access, action: RelativeDigAction, dimensions: Dimensions): DiggingState {
        val dugBlockPos = action.digPosition(headPositionByAccess[access]!!, dimensions)
        return DiggingState(headPositionByAccess, dugPositions + dugBlockPos)
    }

    /**
     * Creates a [GeneratedPattern] that brings any sample to this state.
     */
    fun toPattern() = GeneratedPattern(headPositionByAccess.keys, dugPositions)

    companion object {
        /**
         * Contains all possible actions supported to expand DiggingStates.
         */
        private val allActions: Collection<Action> = MoveAction.all + RelativeDigAction.getAll(DigRange3D.STRICT)

        /**
         * Creates an initial state based on the given accesses.
         *
         * @param accesses the list of accesses to start digging
         */
        fun initialState(accesses: Collection<Access>): DiggingState {
            val headPosByAccess = HashMap<Access, Position>(accesses.size)
            val dugPositions = ArrayList<Position>(accesses.size * 2)
            for (access in accesses) {
                headPosByAccess[access] = access.head
                dugPositions.add(access.head)
                dugPositions.add(access.feet)
            }
            return DiggingState(headPosByAccess, dugPositions)
        }
    }
}
