package org.hildan.minecraft.mining.optimizer.patterns.generated.actions;

import org.hildan.minecraft.mining.optimizer.chunks.Block;
import org.hildan.minecraft.mining.optimizer.chunks.Sample;

public interface Action {

    /**
     * Checks whether it is possible to applyTo this action in the given situation.
     *
     * @param sample
     *         the current sample
     * @param currentHeadPosition
     *         the current position of the head of the player
     * @return true if this action can be performed in the given situation
     */
    boolean isValidFor(Sample sample, Block currentHeadPosition);

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
    Block applyTo(Sample sample, Block currentHeadPosition) throws IllegalStateException;
}
