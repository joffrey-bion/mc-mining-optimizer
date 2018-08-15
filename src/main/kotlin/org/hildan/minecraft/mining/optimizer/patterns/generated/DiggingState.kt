package org.hildan.minecraft.mining.optimizer.patterns.generated

import org.hildan.minecraft.mining.optimizer.blocks.Sample
import org.hildan.minecraft.mining.optimizer.geometry.Position
import org.hildan.minecraft.mining.optimizer.patterns.Access
import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern
import org.hildan.minecraft.mining.optimizer.patterns.generated.actions.Action
import org.hildan.minecraft.mining.optimizer.patterns.generated.actions.DigAction
import org.hildan.minecraft.mining.optimizer.patterns.generated.actions.DigRange3D
import org.hildan.minecraft.mining.optimizer.patterns.generated.actions.MoveAction
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

    private val sample: Sample

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
     * Creates an initial state for the given sample based on the given accesses.
     *
     * @param sample the current sample
     * @param accesses the list of accesses to start digging
     */
    constructor(sample: Sample, accesses: Collection<Access>) {
        this.sample = sample
        this.headPositionsPerAccess = HashMap(accesses.size)
        this.actionsPerAccess = HashMap(accesses.size)
        for (access in accesses) {
            sample.digBlock(access.feet)
            sample.digBlock(access.head)
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
        this.sample = Sample(state.sample)
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
    private fun transition(access: Access, action: Action): DiggingState {
        val newState = DiggingState(this)

        val newHeadPosition = action.executeOn(newState.sample, newState.headPositionsPerAccess[access]!!)
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
    private fun isAcceptable(access: Access, action: Action): Boolean {
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
    fun expand(constraints: GenerationConstraints): Collection<DiggingState> {
        val actionsCount = actionsPerAccess.map { it.value.size }.sum()
        if (actionsCount >= constraints.maxActions) {
            return emptyList()
        }
        if (sample.dugBlocksCount >= constraints.maxDugBlocks) {
            return emptyList()
        }
        return headPositionsPerAccess.flatMap { this.expandAccess(it.key) }
    }

    /**
     * Streams the states representing all possible ways of continuing in the given access.
     *
     * @param access the access to operate on
     * @return a Stream of states resulting of each possible action taken on the given access
     */
    private fun expandAccess(access: Access) = allActions
        .filter { action -> isAcceptable(access, action) }
        .map { action -> transition(access, action) }

    /**
     * Creates a [GeneratedPattern] that brings any sample to this state.
     *
     * @return a [GeneratedPattern] that brings any sample to this state.
     */
    fun toPattern() = GeneratedPattern(actionsPerAccess, sample.width, sample.height, sample.length)



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

        if (sample != other.sample) return false
        if (headPositionsPerAccess != other.headPositionsPerAccess) return false

        return true
    }

    override fun hashCode(): Int {
        var result = sample.hashCode()
        result = 31 * result + headPositionsPerAccess.hashCode()
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
