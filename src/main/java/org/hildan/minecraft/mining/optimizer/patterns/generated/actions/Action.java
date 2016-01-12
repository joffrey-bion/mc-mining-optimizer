package org.hildan.minecraft.mining.optimizer.patterns.generated.actions;

import org.hildan.minecraft.mining.optimizer.chunks.Block;
import org.hildan.minecraft.mining.optimizer.chunks.Sample;
import org.hildan.minecraft.mining.optimizer.geometry.Position;

/**
 * Represents an action the player can perform. An action is performed on a sample, from a given position.
 */
public interface Action {

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
}
