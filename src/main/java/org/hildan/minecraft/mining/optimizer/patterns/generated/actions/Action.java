package org.hildan.minecraft.mining.optimizer.patterns.generated.actions;

import org.hildan.minecraft.mining.optimizer.blocks.Sample;
import org.hildan.minecraft.mining.optimizer.geometry.Position;

/**
 * Represents an action the player can perform. An action is performed on a sample, from a given position.
 */
public interface Action {

    /**
     * Returns whether this action modifies the sample it is applied on, and thus has a direct impact on the pattern.
     * For instance, moving does not affect the sample, but digging does.
     * <p>
     * More formally, this method returns true if {@link #executeOn} modifies the sample passed as a parameter.
     *
     * @return true if this action modifies the sample it is applied on
     */
    boolean affectsSample();

    /**
     * Checks whether it is possible to execute this action in the given situation.
     *
     * @param sample
     *         the current sample
     * @param currentHeadPosition
     *         the current position of the head of the player
     * @return true if this action can be performed in the given situation
     */
    boolean isValidFor(Sample sample, Position currentHeadPosition);

    /**
     * Executes this action on the given sample.
     *
     * @param sample
     *         the current sample
     * @param currentHeadPosition
     *         the current position of the head of the player
     * @return the new position of the head of the player
     * @throws IllegalStateException
     *         if the action couldn't be performed in the current state
     */
    Position executeOn(Sample sample, Position currentHeadPosition) throws IllegalStateException;

    /**
     * Returns whether this action is the inverse of the given action.
     * <p>
     * More formally, this method returns true if, when applying this action and the given action on the same sample,
     * the sample gets back into the same state.
     *
     * @return true if this action modifies the sample it is applied on
     */
    boolean isInverseOf(Action action);
}
