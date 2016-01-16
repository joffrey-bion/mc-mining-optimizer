package org.hildan.minecraft.mining.optimizer.patterns.generated;

import org.hildan.minecraft.mining.optimizer.chunks.Sample;
import org.hildan.minecraft.mining.optimizer.geometry.Position;
import org.hildan.minecraft.mining.optimizer.patterns.DiggingPattern;
import org.hildan.minecraft.mining.optimizer.patterns.generated.actions.Action;
import org.hildan.minecraft.mining.optimizer.patterns.generated.actions.DigAction;
import org.hildan.minecraft.mining.optimizer.patterns.generated.actions.DigRange3D;
import org.hildan.minecraft.mining.optimizer.patterns.generated.actions.MoveAction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a state while digging a sample. It contains the state of the sample, the position of the player and the
 * actions done so far to arrive at this state. It can generate the next possible states from this point using {@link
 * #expand}.
 */
class DiggingState {

    private static final Collection<Action> allActions;

    static {
        Collection<Action> actions = new HashSet<>();
        actions.addAll(MoveAction.getAll());
        actions.addAll(DigAction.getAll(DigRange3D.STRICT));
        allActions = Collections.unmodifiableCollection(actions);
    }

    private final Position currentHeadPosition;

    private final Sample sample;

    private final List<Action> actionsPerformed;

    /**
     * Creates a new state with the given sample and player's position, and an empty list of actions.
     *
     * @param currentHeadPosition
     *         the current position of the head of the player
     * @param sample
     *         the current sample
     */
    DiggingState(Position currentHeadPosition, Sample sample) {
        this(currentHeadPosition, sample, new ArrayList<>());
    }

    /**
     * Creates a new state with the given sample, player's position, and list of actions performed so far.
     *
     * @param currentHeadPosition
     *         the current position of the head of the player
     * @param sample
     *         the current sample
     * @param actionsPerformed
     *         the list of actions that were performed to arrive in this state
     */
    private DiggingState(Position currentHeadPosition, Sample sample, List<Action> actionsPerformed) {
        this.currentHeadPosition = currentHeadPosition;
        this.sample = sample;
        this.actionsPerformed = actionsPerformed;
    }

    /**
     * Returns the {@code DiggingState} resulting of the execution of the given action on this state. This method does
     * not affect this state.
     *
     * @param action
     *         the action to perform on this state
     * @return the resulting state
     */
    private DiggingState transition(Action action) {
        Sample newSample = new Sample(sample);
        Position newHeadPosition = action.executeOn(newSample, currentHeadPosition);
        List<Action> newActions = new ArrayList<>(actionsPerformed);
        newActions.add(action);
        return new DiggingState(newHeadPosition, newSample, newActions);
    }

    /**
     * Expands the current state by performing every possible action on it. This method does not affect this state.
     *
     * @return the collection of all states resulting of the execution of each possible action on this state.
     */
    Collection<DiggingState> expand() {
        return allActions.stream()
                         .filter(a -> a.isValidFor(sample, currentHeadPosition))
                         .map(this::transition)
                         .collect(Collectors.toList());
    }

    /**
     * Creates a {@link GeneratedPattern} that brings any sample to this state.
     *
     * @return a {@link GeneratedPattern} that brings any sample to this state.
     */
    DiggingPattern toPattern() {

        // FIXME find a way to give these arguments...

        return new GeneratedPattern(null, 0, 0, 0);
    }
}
