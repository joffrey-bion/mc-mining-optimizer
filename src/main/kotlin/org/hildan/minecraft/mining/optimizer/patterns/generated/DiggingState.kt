package org.hildan.minecraft.mining.optimizer.patterns.generated

import org.hildan.minecraft.mining.optimizer.blocks.Sample
import org.hildan.minecraft.mining.optimizer.geometry.DigRange3D
import org.hildan.minecraft.mining.optimizer.geometry.Dimensions
import org.hildan.minecraft.mining.optimizer.geometry.Position
import org.hildan.minecraft.mining.optimizer.patterns.Access
import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern
import java.util.HashMap

/**
 * Contains all possible actions supported with [DigRange3D.STRICT] digging range.
 */
val EXPANDING_ACTIONS: List<Action> = allActionsFor(DigRange3D.STRICT) // TODO makes this configurable, but efficiently

/**
 * Represents a state while digging a sample. It contains the history that brought to this state and can be
 * [replayed on][replayOn] a [Sample]. It can generate the next possible states from this point using [expand].
 *
 * Can be turned into a [DiggingPattern] using [toPattern].
 */
internal data class DiggingState(
    /**
     * When multiple accesses are available, we track the head position for each access independently. Hence one head
     * position per access.
     */
    private val headPositionByAccess: Map<Access, Position>,

    private val dugPositions: Set<Position>
) {
    fun replayOn(sample: DigMatrix) {
        dugPositions.forEach { sample.dig(it) }
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
    fun expand(sample: DigMatrix, constraints: GenerationConstraints): Collection<DiggingState> {
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
    private fun expandAccess(access: Access, sample: DigMatrix) = EXPANDING_ACTIONS
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
    private fun next(sample: DigMatrix, access: Access, action: Action): DiggingState = when (action) {
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
}

/**
 * Creates an initial digging state based on the given [accesses].
 */
internal fun initialState(accesses: Collection<Access>): DiggingState {
    val headPosByAccess = HashMap<Access, Position>(accesses.size)
    val dugPositions = HashSet<Position>(accesses.size * 2)
    for (access in accesses) {
        headPosByAccess[access] = access.head
        dugPositions.add(access.head)
        dugPositions.add(access.feet)
    }
    return DiggingState(headPosByAccess, dugPositions)
}
