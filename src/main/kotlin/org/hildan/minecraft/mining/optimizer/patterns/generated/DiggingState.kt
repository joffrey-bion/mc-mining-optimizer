package org.hildan.minecraft.mining.optimizer.patterns.generated

import org.hildan.minecraft.mining.optimizer.blocks.Sample
import org.hildan.minecraft.mining.optimizer.geometry.Position
import org.hildan.minecraft.mining.optimizer.patterns.Access
import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern
import org.hildan.minecraft.mining.optimizer.patterns.generated.actions.Action
import org.hildan.minecraft.mining.optimizer.patterns.generated.actions.DigAction
import org.hildan.minecraft.mining.optimizer.patterns.generated.actions.DigRange3D
import org.hildan.minecraft.mining.optimizer.patterns.generated.actions.MoveAction
import org.hildan.minecraft.mining.optimizer.patterns.generated.actions.executeOn
import java.util.ArrayList
import java.util.HashMap

/**
 * Represents a state while digging a sample. It contains the state of the sample, the position of the player and the
 * actions done so far to arrive at this state. It can generate the next possible states from this point using [expand].
 *
 *
 * Can be turned into a [DiggingPattern].
 */
internal class DiggingState {

    /**
     * When multiple accesses are available, we track the head position for each access independently. Hence one head
     * position per access.
     */
    private val headPositionsPerAccess: MutableMap<Access, Position>

    private val actionsPerAccess: MutableMap<Access, MutableList<Action>>

    /**
     * Returns whether this state is canonical. This means that we can't remove any of the last actions without changing
     * the resulting pattern. In other words, for every access, the last action must be a dig action, not a move.
     *
     * @return true if this state is canonical
     */
    val isCanonical: Boolean
        get() = actionsPerAccess.all { (_, l) -> l.isEmpty() || l.last() is DigAction }

    /**
     * Creates an initial state based on the given accesses.
     *
     * @param accesses the list of accesses to start digging
     */
    constructor(accesses: Collection<Access>) {
        this.headPositionsPerAccess = HashMap(accesses.size)
        this.actionsPerAccess = HashMap(accesses.size)
        for (access in accesses) {
            headPositionsPerAccess[access] = access.head
            actionsPerAccess[access] = mutableListOf()
        }
    }

    /**
     * Creates a copy of the given state.
     *
     * @param state the state to copy
     */
    private constructor(state: DiggingState) {
        this.headPositionsPerAccess = HashMap(state.headPositionsPerAccess)
        this.actionsPerAccess = state.actionsPerAccess.mapValuesTo(HashMap()) { ArrayList(it.value) }
    }

    /**
     * Returns the `DiggingState` resulting of the execution of the given action on this state. This method does
     * not affect this state.
     *
     * @param access the access for which to add the action
     * @param action the action to perform on this state
     * @return the resulting state
     */
    private fun transition(sample: Sample, access: Access, action: Action): DiggingState {
        val newState = DiggingState(this)

        val newHeadPosition = action.executeOn(sample, newState.headPositionsPerAccess[access]!!)
        newState.headPositionsPerAccess[access] = newHeadPosition
        newState.actionsPerAccess[access]!!.add(action)
        return newState
    }

    /**
     * Returns whether it is acceptable to execute this action in the current situation.
     *
     * @param access the access for which to execute the action
     * @param action the action to perform
     * @return true if this action may be performed in the current situation
     */
    private fun isAcceptable(sample: Sample, access: Access, action: Action): Boolean {
        val actions = actionsPerAccess[access]!!
        if (!actions.isEmpty() && action.isInverseOf(actions.last())) {
            return false
        }
        val currentHeadPosition = headPositionsPerAccess[access]!!
        return action.isValidFor(sample, currentHeadPosition)
    }

    /**
     * Expands the current state by performing every possible action on it. This method does not affect this state.
     *
     * @return the collection of all states resulting of the execution of each possible action on this state.
     */
    fun expand(sample: Sample, constraints: GenerationConstraints): Collection<DiggingState> {
        val actionsCount = actionsPerAccess.map { it.value.size }.sum()
        if (actionsCount >= constraints.maxActions) {
            return emptyList()
        }
        if (sample.dugBlocksCount >= constraints.maxDugBlocks) {
            return emptyList()
        }
        return headPositionsPerAccess.flatMap { this.expandAccess(it.key, sample) }
    }

    fun replayOn(sample: Sample) {
        actionsPerAccess.forEach { (access, actions) ->
            access.digInto(sample)
            actions.executeOn(sample, access.head)
        }
    }

    /**
     * Streams the states representing all possible ways of continuing in the given access.
     *
     * @param access the access to operate on
     * @return a Stream of states resulting of each possible action taken on the given access
     */
    private fun expandAccess(access: Access, sample: Sample) = allActions
        .filter { action -> isAcceptable(sample, access, action) }
        .map { action -> transition(sample, access, action) }

    /**
     * Creates a [GeneratedPattern] that brings any sample to this state.
     *
     * @return a [GeneratedPattern] that brings any sample to this state.
     */
    fun toPattern(width: Int, height: Int, length: Int) = GeneratedPattern(actionsPerAccess, width, height, length)

    override fun toString(): String {
        val sb = StringBuilder()
        val indent = "   "
        for ((access, actions) in actionsPerAccess) {
            sb.append(access).append(String.format("%n"))
            for (action in actions) {
                sb.append(indent).append(action).append(String.format("%n"))
            }
        }
        return sb.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DiggingState

        if (headPositionsPerAccess != other.headPositionsPerAccess) return false
        if (actionsPerAccess != other.actionsPerAccess) return false

        return true
    }

    override fun hashCode(): Int {
        var result = headPositionsPerAccess.hashCode()
        result = 31 * result + actionsPerAccess.hashCode()
        return result
    }

    companion object {
        /**
         * Contains all possible actions supported to expand DiggingStates.
         */
        private val allActions: Collection<Action>

        init {
            allActions = mutableSetOf()
            allActions.addAll(MoveAction.all)
            allActions.addAll(DigAction.getAll(DigRange3D.STRICT))
        }
    }
}
